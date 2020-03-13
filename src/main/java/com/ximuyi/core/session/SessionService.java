package com.ximuyi.core.session;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.core.actor.AkkaActor;
import com.ximuyi.core.actor.message.MsChannelChange;
import com.ximuyi.core.actor.message.MsChannelCommand;
import com.ximuyi.core.actor.message.MsChannelOpen;
import com.ximuyi.core.actor.message.MsUserLogout;
import com.ximuyi.core.api.login.AccountInfo;
import com.ximuyi.core.api.login.LoginResult;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.CommandUtil;
import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.command.response.ByteResponse;
import com.ximuyi.core.config.ConfigKey;
import com.ximuyi.core.config.Configs;
import com.ximuyi.core.ContextResolver;
import com.ximuyi.core.session.channel.ChannelCloseReason;
import com.ximuyi.core.session.channel.ChannelUtil;
import com.ximuyi.core.session.channel.NetChannel;
import com.ximuyi.core.user.IUser;
import com.ximuyi.core.user.InnerUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class SessionService extends AkkaActor {
    public static final String IDENTIFY = "session";

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private static long TIME_OUT = Configs.getInstance().getInteger(ConfigKey.AKKA_SESSION_TIMEOUT, 60) * 1000L;
    private static long SECOND_TICK = Configs.getInstance().getInteger(ConfigKey.AKKA_SESSION_TICK, 5);

    private final Map<Long, ChannelUnLogin> channelUnLoginMap;
    private final Cancellable cancellable;

    public SessionService() {
        this.channelUnLoginMap = new HashMap<>();
        Scheduler scheduler = ContextResolver.getActorSystem().scheduler();
        FiniteDuration duration = Duration.create(SECOND_TICK, TimeUnit.SECONDS);
        cancellable = scheduler.schedule(duration, duration, getSelf(), new ChannelCheck(),
                getContext().dispatcher(), getSelf());
    }

    @Override
    protected void onReceiveBuilder(ReceiveBuilder builder) {
        builder.match(MsChannelOpen.class, this::onChannelOpen)
        .match(ChannelCheck.class, this::onCheckChannelUnLogin)
        .match(MsChannelCommand.class, this::onCommandUpMessage)
        .match(MsUserLogout.class, this::onUserLogoutSuccess);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        cancellable.cancel();
    }

    private void onChannelOpen(MsChannelOpen message){
        NetChannel netChannel = message.getChannel();
        ChannelUnLogin channelUnLogin  = new ChannelUnLogin(netChannel);
        this.channelUnLoginMap.put(channelUnLogin.getUniqueId(), channelUnLogin);
    }

    /**
     * 登陆验证、ActorRef的创建都需要是单线程的。
     * 这里的瓶颈之后需要优化：使用登陆线程池
     * @param channelCommand
     */
    @SuppressWarnings("unchecked")
    private void onCommandUpMessage(MsChannelCommand channelCommand){
        NetChannel netChannel = channelCommand.getNetChannel();
        ChannelUnLogin channelUnLogin = channelUnLoginMap.get(netChannel.getUniqueId());
        if (channelUnLogin == null){
            return;
        }
        if (!channelUnLogin.netChannel.equals(netChannel)) {
            return;
        }
        ICommand command = channelCommand.getCommand();
        if (!command.equals(CommandUtil.LOGIN)){
            logger.error("channel:{} cmd:{} error.", netChannel.getUniqueId(), command);
            return;
        }
        try {
            Object request = channelCommand.getMessage();
            AccountInfo accountInfo = ContextResolver.getUserHelper().getAccountInfo(request);
            UserSession session = SessionManager.getInstance().getUserSession(accountInfo.getUserId());
            LoginResult loginResult = ContextResolver.getUserHelper().doUserLogin(request, session != null);
            if (loginResult.getResultCode().isSuccess()) {
                IUser extUser = loginResult.getExtUser();
                if (session == null){
                    // 第一次登陆
                    InnerUser innerUser = new InnerUser(extUser);
                    ActorRef actorRef = getContext().actorOf(Props.create(AkkaUser.class, innerUser), "user" + extUser.getUserId());
                    getContext().watch(actorRef);
                    SessionManager.getInstance().addUserSession(new UserSession(actorRef, innerUser, netChannel));
                }
                else if (session.getNetChannel().equals(netChannel)){

                }
                else {
                    session.getSelf().tell(new MsChannelChange(netChannel, loginResult.getConnectWay()), getSelf());
                }
                channelUnLoginMap.remove(netChannel.getUniqueId());
            }
            netChannel.addCommandResponse(new ByteResponse(CommandUtil.LOGIN, loginResult.getResultCode(), loginResult.getMessage()));
        }
        catch (Throwable t){
            logger.error("checkUserLogin error, cmd:{} message:{}", channelCommand.getCommand(), channelCommand.getMessage(), t);
            netChannel.addCommandResponse(new ByteResponse(CommandUtil.LOGIN, ResultCode.SEVER_EXCEPTION, null));
        }
    }


    private void onCheckChannelUnLogin(ChannelCheck message){
        long currentTime = System.currentTimeMillis();
        List<Long> removeUniqueIdList = new LinkedList<>();
        for (Map.Entry<Long, ChannelUnLogin> entry : channelUnLoginMap.entrySet()) {
            if (currentTime < entry.getValue().getExpireTime()){
                continue;
            }
            NetChannel netChannel = entry.getValue().netChannel;
            ChannelUtil.channelClose(netChannel, ChannelCloseReason.LOGIN_TIME_OUT, false);
            removeUniqueIdList.add(entry.getKey());
        }
        removeUniqueIdList.forEach(channelUnLoginMap::remove);
        message.count++;
        if (!removeUniqueIdList.isEmpty()){
            logger.debug("channel idle check count:{}, removed uniqueIds:{}", message.count, removeUniqueIdList);
        }
    }

    private void onUserLogoutSuccess(MsUserLogout message){
        SessionManager.getInstance().removeUserSession(message.getUserId());
        getContext().stop(getSender());
    }

    private static class ChannelCheck {

        private long count;
    }

    private static class ChannelUnLogin {
        private final long expireTime;
        private final NetChannel netChannel;

        public ChannelUnLogin(NetChannel netChannel) {
            this.netChannel = netChannel;
            this.expireTime = System.currentTimeMillis() + TIME_OUT;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public long getUniqueId() {
            return netChannel.getUniqueId();
        }
    }
}
