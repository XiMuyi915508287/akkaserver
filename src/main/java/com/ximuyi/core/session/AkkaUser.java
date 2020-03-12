package com.ximuyi.core.session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Consumer;

import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.core.actor.ActorRunnable;
import com.ximuyi.core.actor.AkkaActor;
import com.ximuyi.core.actor.message.MsChannelChange;
import com.ximuyi.core.actor.message.MsChannelClose;
import com.ximuyi.core.actor.message.MsChannelCommand;
import com.ximuyi.core.api.login.ConnectWay;
import com.ximuyi.core.command.handler.CommandHandlerUtil;
import com.ximuyi.core.command.handler.ICommandHandler;
import com.ximuyi.core.core.ContextResolver;
import com.ximuyi.core.session.channel.ChannelCloseReason;
import com.ximuyi.core.session.channel.ChannelUtil;
import com.ximuyi.core.session.channel.NetChannel;
import com.ximuyi.core.user.InnerUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AkkaUser extends AkkaActor {

    private static final Logger logger = LoggerFactory.getLogger(AkkaUser.class);

    private final InnerUser innerUser;
    private long operationTime;

    public AkkaUser(InnerUser innerUser) {
        this.innerUser = innerUser;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        logger.debug("akka started");
        intAkkaContextInvoke( session -> ContextResolver.getUserHelper().onUserLogin(innerUser.getExUser()));
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        logger.debug("akka stopped");
    }

    @Override
    protected void onReceiveBuilder(ReceiveBuilder builder) {
        Method[] methods = this.getClass().getDeclaredMethods();
        final AkkaUser object = this;
        for (Method method : methods) {
            Parameter[] parameters = method.getParameters();
            if (parameters == null || parameters.length != 2){
                continue;
            }
            Class<?> aClass0 = parameters[0].getType();
            if (!aClass0.isAssignableFrom(UserSession.class)){
                continue;
            }
            Class<?> aClass1 = parameters[1].getType();
            if (aClass1.isPrimitive() || aClass1.isAssignableFrom(String.class)){
                continue;
            }
            builder.match(aClass1, message -> intAkkaContextInvoke(session -> {
                try {
                    debug(session,method.getName() + " handle msg");
                    method.invoke(object, session, message);
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    error(null, "method.invoke error", e);
                }
            }));
        }
    }

    /**
     * 初始化 AkkaUserContext
     * @param consumer
     */
    private void intAkkaContextInvoke(Consumer<UserSession> consumer){
        UserSession session = SessionManager.getInstance().getUserSession(innerUser.getUserId());
        if (session == null){
            error(null, "session error");
        }
        else {
            try {
                AkkaUserContext.init(session);
                consumer.accept(session);
            }
            catch (Throwable t){
                error(session,"handle msg error", t);
            }
            finally {
                AkkaUserContext.clear();
            }
        }
    }

    /**
     * 处理上行消息
     * @param session
     * @param channelCommand
     */
    @SuppressWarnings("rawtypes")
    private void handleUpCommand(UserSession session, MsChannelCommand channelCommand){
        ICommandHandler handler = CommandHandlerUtil.lookUp(channelCommand.getCommand());
        if (handler == null){
            error(session, "unknown cmd:" + channelCommand.getCommand());
        }
        else {
            updateOperationTime();
            handler.execute(session, channelCommand.getMessage());
        }
    }

    /**
     * 切换了一个新Channel
     * @param session
     * @param channelChange
     */
    private void onChannelChange(UserSession session, MsChannelChange channelChange){
        NetChannel channel = channelChange.getChannel();
        NetChannel oldChannel = session.getNetChannel();
        if (channelChange.getConnectWay().equals(ConnectWay.Outside)){
            // 如果新的连接是从外面登陆的，提示上一个连接被踢出
            ChannelUtil.channelClose(oldChannel, ChannelCloseReason.LOGIN_AGAIN, true);
        }
        session.setNetChannel(channel);
        error(session, "replaced channel:" + oldChannel.getUniqueId());
        if (oldChannel.isBindClosed()){
            // 如果旧的连接是断开的了，那么这里就是重连了~
            ContextResolver.getUserHelper().onReconnect(innerUser.getExUser(), channelChange.getConnectWay());
        }
    }

    /**
     * 连接被外部断开了~
     * @param channelClose
     */
    private void onChannelClosed(UserSession session, MsChannelClose channelClose){
        NetChannel netChannel = session.getNetChannel();
        if (channelClose.getChannelUniqueId() > 0 && channelClose.getChannelUniqueId() != netChannel.getUniqueId()){
            //已经被重新绑定一个了
            return;
        }
        if (netChannel.isBindClosed()){
            return;
        }
        ChannelUtil.channelClose(netChannel, channelClose.getReason(), false);
        ContextResolver.getUserHelper().onDisconnect(innerUser.getExUser());
        netChannel.setBindClosed();
    }

    /**
     *
     * @param session
     * @param runnable
     */
    private void onActorRunnable(UserSession session, ActorRunnable runnable){
        if (runnable.getActorRef() != self()){
            error(session, "runnable actor error");
        }
        runnable.run();
    }


    private void updateOperationTime() {
        this.operationTime = System.currentTimeMillis();
    }

    private void debug(UserSession session, String message){
        logger.debug("{}, userId:{} account:{} channel:{} actor:{}.", message, innerUser.getUserId(), innerUser.getAccount(),
                session == null ? 0 : session.getChannelUniqueId(), getSelf().path());
    }

    private void error(UserSession session, String message){
        logger.error("{}, userId:{} account:{} channel:{} actor:{}.", message, innerUser.getUserId(), innerUser.getAccount(),
                session == null ? 0 : session.getChannelUniqueId(), getSelf().path());
    }

    private void error(UserSession session, String message, Throwable t){
        logger.error("{}, userId:{} account:{} channel:{} actor:{}.", message, innerUser.getUserId(), innerUser.getAccount(),
                session == null ? 0 : session.getChannelUniqueId(), getSelf().path(), t);
    }
}
