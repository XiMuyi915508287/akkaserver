package com.ximuyi.akkaserver.session;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.IAkkaUser;
import com.ximuyi.akkaserver.IUser;
import com.ximuyi.akkaserver.actor.AkkaActor;
import com.ximuyi.akkaserver.api.ITaskManager;
import com.ximuyi.akkaserver.coder.ResponseCode;
import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.extension.CommandUtil;
import com.ximuyi.akkaserver.extension.request.IRequestHandler;
import com.ximuyi.akkaserver.extension.request.RequestHandlerUtil;
import com.ximuyi.akkaserver.io.IoDownPackage;
import com.ximuyi.akkaserver.io.IoUpChannelClose;
import com.ximuyi.akkaserver.io.IoUpMessage;
import com.ximuyi.akkaserver.message.MsAkkaAttach;
import com.ximuyi.akkaserver.message.MsAkkaSession;
import com.ximuyi.akkaserver.message.MsAkkaUserLogin;
import com.ximuyi.akkaserver.message.MsChannelClose;
import com.ximuyi.akkaserver.session.channel.ChannelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class AkkaUser extends AkkaActor implements IAkkaUser, Runnable{

    private static final Logger logger = LoggerFactory.getLogger(AkkaUser.class);

    //逻辑层的User
    private final IUser extUser;
    //新建User的回调
    private final IAkkaUserLife akkaUserLife;
    //底层判断是否登出了
    private volatile boolean isLogin = false;
    //任务调度
    private final TaskManager taskManager;
    //上一次操作时间
    private volatile long operationTime;
    //Akka成功绑定
    private ChannelSession session;
    private Cancellable cancellable;

    public AkkaUser(ChannelSession session, IUser extUser, IAkkaUserLife akkaUserLife) {
        this.session = session;
        this.extUser = extUser;
        this.akkaUserLife = akkaUserLife;
        this.taskManager = new TaskManager(getSelf(), getContext());
    }

    @Override
    public IUser getExtUser() {
        return extUser;
    }

    @Override
    public String getAccount() {
        return extUser.getAccount();
    }

    @Override
    public long getUserId() {
        return extUser.getUserId();
    }

    @Override
    public ITaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public boolean sendIoMessage(IoDownPackage message) {
        return session.sendIoMessage(message);
    }

    @Override
    public boolean isLogin() {
        return isLogin;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.akkaUserLife.onCreate(this);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        akkaUserLife.onRemove(this);
        if (cancellable != null){
            cancellable.cancel();
        }
    }

    @Override
    public void run() {
        if (!operationTimeOut(60000L)){
            return;
        }
        _handleIdleLogout();
    }

    @Override
    public void tell(Object message, ActorRef sender) {
        getSelf().tell(message, sender);
    }

    @Override
    protected void buildAkkaReceive(ReceiveBuilder builder) {
        builder.match(IoUpMessage.class, message-> _handleCommand(message));
        builder.match(MsAkkaUserLogin.class, message-> _handleOnLogin(message));
        builder.match(MsAkkaSession.class, session-> _setSession(session));
        builder.match(MsChannelClose.class, message-> _closeSession(message));
        //有可能ActorRef已经存在了，但是AkkaUser还没有构造成功，所以信息只能从ActorRef发送然后定位到这里
        builder.match(IoDownPackage.class, message-> sendIoMessage(message));
    }

    @Override
    public IAkkaUser getAkkaUser() {
        return this;
    }

    private void _handleCommand(IoUpMessage ioPackage){
        if (!isRightSession()){
            return;
        }
        if (CommandUtil.equals(ioPackage.getCmd(), CommandConst.LOGOUT)){
            _handleIdleLogout();
        }
        else if (isLogin()){
            IRequestHandler handler = RequestHandlerUtil.find(ioPackage.getCmd());
            updateOperationTime();
            handler.handleRequest(this, ioPackage.getMessage());
        }
    }

    private void _handleOnLogin(MsAkkaUserLogin akkaUserLogin){
        if (!isRightSession()) {
            return;
        }
        if (isLogin()){
            logger.error("user[{}] onLogin error", getUserId());
            return;
        }
        setLogin(true);
        updateOperationTime();
        cancellable = getTaskManager().schedule(3, 3, TimeUnit.SECONDS, this);
        try {
            ContextResolver.getUserHelper().onLogin(extUser);
        }
        catch (Throwable t){
            logger.error("user[{}] onLogin error", getUserId(), t);
        }
        finally {
            logger.debug("user[{}] channel[{}] login success.", getUserId(), session.getUniqueId());
        }
    }


    private void _handleIdleLogout(){
        if (!isLogin()){
            return;
        }
        cancellable.cancel();
        setLogin(false);
        try {
            ContextResolver.getUserHelper().onLogout(extUser);
        }
        catch (Throwable t){
            logger.error("user[{}] onLogout error", getUserId(), t);
        }
        finally {
            logger.error("user[{}] channel[{}] logout sucess", getUserId(), session.getUniqueId());
        }
        session.tell(new IoUpChannelClose(true, "logout"), getSelf());
        getContext().stop(getSelf());
    }

    private void _setSession(MsAkkaSession akkaSession){
        if (!isLogin()){
            //没登录之前，不能设置Session的
            akkaSession.onCallback(ResponseCode.SEVER_EXCEPTION);
            return;
        }
        ChannelSession newSession = (ChannelSession)akkaSession.getSession();
        if (session != newSession){
            logger.debug("user[{}] change channel, {}->{}", getUserId(), session.getUniqueId(), newSession.getUniqueId());
            //已经断开了
            session.tell(new IoUpChannelClose(true, "kicked"), getSelf());
            session = newSession;
        }
        else {
            logger.debug("user[{}] set channel {}", getUserId(), session.getUniqueId());
        }
        session.tell(new MsAkkaAttach(this), getSelf());
        akkaSession.onCallback(ResponseCode.SUCCESS);
    }

    private void _closeSession(MsChannelClose channelClose){
        if (isRightSession()){
        }
    }

    private boolean isRightSession(){
        return getSender() == session.getSelf();
    }

    private void setLogin(boolean login) {
        isLogin = login;
    }


    private boolean operationTimeOut(long durableTime) {
        return (System.currentTimeMillis() - operationTime) >= durableTime;
    }

    private void updateOperationTime() {
        this.operationTime = System.currentTimeMillis();
    }
}
