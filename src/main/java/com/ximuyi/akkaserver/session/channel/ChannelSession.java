package com.ximuyi.akkaserver.session.channel;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.IAkkaUser;
import com.ximuyi.akkaserver.actor.AkkaActor;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.extension.CommandUtil;
import com.ximuyi.akkaserver.extension.request.IRequestHandler;
import com.ximuyi.akkaserver.extension.request.RequestHandlerUtil;
import com.ximuyi.akkaserver.extension.response.IoDChannelClose;
import com.ximuyi.akkaserver.io.IoDownPackage;
import com.ximuyi.akkaserver.io.IoUpChannelClose;
import com.ximuyi.akkaserver.io.IoUpMessage;
import com.ximuyi.akkaserver.message.MsAkkaAttach;
import com.ximuyi.akkaserver.session.AkkaUser;
import com.ximuyi.akkaserver.session.ISession;
import com.ximuyi.akkaserver.session.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelSession extends AkkaActor implements ISession {

    private static final Logger logger = LoggerFactory.getLogger(ChannelSession.class);
    private final TaskManager taskManager;
    //是否销毁了
    private AtomicBoolean isDispose = new AtomicBoolean(false);
    private volatile AkkaUser akkaUser;
    private final ChannelInteractor decorator;

    public ChannelSession(ChannelInteractor decorator) {
        this.decorator = decorator;
        this.taskManager = new TaskManager(getSelf(), getContext());
    }

    public long getUniqueId() {
        return decorator.getUniqueId();
    }

    @Override
    public boolean sendIoMessage(IoDownPackage message) {
        return decorator.sendIoMessage(message);
    }

    @Override
    public void tell(Object message, ActorRef sender) {
        if (decorator.isClose() || isDispose.get()){
            return;
        }
        getSelf().tell(message, sender);
    }

    @Override
    public IAkkaUser getAkkaUser() {
        return akkaUser;
    }

    @Override
    public boolean isLogin() {
        return akkaUser != null;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        decorator.setSession(this);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        isDispose.set(true);
        channelClose(new IoUpChannelClose(true, "stop"));
    }

    @Override
    protected void buildAkkaReceive(ReceiveBuilder builder) {
        builder.match(IoUpMessage.class, message-> _handleCommand(message));
        builder.match(MsAkkaAttach.class, message -> _handleAkkaUser(message));
    }

    public boolean isDispose() {
        return isDispose.get();
    }

    private void _handleAkkaUser(MsAkkaAttach akkaAttach){
        if (decorator.isClose() || isDispose.get()){
            return;
        }
        AkkaUser akkaUser0 = akkaAttach.getAkkaUser();
        logger.debug("channel[{}] attach akkaUser {}->{}", getUniqueId(), akkaUser == null ? 0 : akkaUser.getUserId(),
                     akkaUser0 == null ? 0 : akkaUser0.getUserId());
        this.akkaUser = akkaUser0;
    }

    private void _handleCommand(IoUpMessage ioPackage){
        if (decorator.isClose() || isDispose.get()){
            return;
        }
        if (CommandUtil.equals(ioPackage.getCmd(), CommandConst.CHANNEL_CLOSE)){
            channelClose((IoUpChannelClose)ioPackage);
            return;
        }
        IRequestHandler handler = RequestHandlerUtil.find(ioPackage.getCmd());
        if (handler == null){
            logger.error("channel[{}] can't find the cmd: {}", getUniqueId(), ioPackage.getCmd().toString());
            return;
        }
        if (CommandUtil.equals(ioPackage.getCmd(), CommandConst.LOGIN)){
            handler.handleRequest(this, ioPackage.getMessage());
        }
        else if (akkaUser != null){
            if (handler.isThreadSafe()){
                handler.handleRequest(akkaUser, ioPackage.getMessage());
            }
            else {
                akkaUser.tell(ioPackage, getSelf());
            }
        }
        else {
            channelClose(new IoUpChannelClose(true, "cmd error"));
        }
    }

    private void channelClose(IoUpChannelClose msChannel){
        if (decorator.isClose()){
            return;
        }
        this.akkaUser = null;//关掉之后，自己设置掉为null
        if (msChannel.isNotifyClient()){
            if (decorator.sendIoMessage(new IoDChannelClose(msChannel.getReason()))){
                taskManager.scheduleOnce(2, TimeUnit.SECONDS, ()-> doChannnelClose(msChannel.getReason()));
            }
            else {
                doChannnelClose(msChannel.getReason());
            }
        }
        else {
            doChannnelClose(msChannel.getReason());
        }
    }

    private void doChannnelClose(String reason){
        decorator.disconnect(reason);
        if (isDispose.compareAndSet(false, true)) {
            getContext().stop(getSelf());
        }
    }
}
