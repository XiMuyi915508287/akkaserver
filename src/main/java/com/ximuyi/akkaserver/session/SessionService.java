package com.ximuyi.akkaserver.session;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.actor.AkkaActor;
import com.ximuyi.akkaserver.config.ConfigKey;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.io.IoUpChannelClose;
import com.ximuyi.akkaserver.message.MsChannelNew;
import com.ximuyi.akkaserver.session.channel.ChannelInteractor;
import com.ximuyi.akkaserver.session.channel.ChannelSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SessionService extends AkkaActor {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    public static final String IDENTIFY = "session";

    private final Map<Long, ChannelUnLogin> unLoginChannels;
    private long sessionTimeout;
    private Cancellable cancellable;

    public SessionService() {
        this.unLoginChannels = new HashMap<>();
    }

    @Override
    public void init(ConfigWrapper config) throws Throwable {
        sessionTimeout = config.getInteger(ConfigKey.AKKA_SESSION_TIMEOUT, 60) * 1000L;
        int checkTime = config.getInteger(ConfigKey.AKKA_SESSION_CHECKTIME, 10);

        ActorSystem system = ContextResolver.getActorSystem();
        Scheduler scheduler = system.scheduler();
        FiniteDuration duration = Duration.create(checkTime, TimeUnit.SECONDS);
        cancellable = scheduler.schedule(duration, duration, getSelf(), new SessionCheck(),
                                         getContext().dispatcher(), getSelf());
    }

    @Override
    protected void buildAkkaReceive(ReceiveBuilder builder) {
        builder.match(MsChannelNew.class, channel -> {
            ChannelInteractor decorator = channel.getDecorator();
            ActorRef actorRef = getContext().actorOf(Props.create(ChannelSession.class, decorator), "channel-"+ decorator.getUniqueId());
            getContext().watch(actorRef);
            ChannelUnLogin channelUnLogin  = new ChannelUnLogin(decorator);
            unLoginChannels.put(channelUnLogin.getUniqueId(), channelUnLogin);
            logger.debug("create session actor {}, ip:{}", decorator.getUniqueId(), decorator.getIpAddress());
        })
        .match(SessionCheck.class, sessionCheck -> onCheckSession())
        ;
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        cancellable.cancel();
    }


    private void onCheckSession(){
        long current = System.currentTimeMillis();
        LinkedList<Long> removeList = new LinkedList<>();
        unLoginChannels.forEach( (key, value)->{
            if (value.isLogin()){
                //已经登陆了，直接移除掉
                removeList.add(value.getUniqueId());
            }
            else if (value.isClose()){
                removeList.add(value.getUniqueId());
            }
            else if (current - value.getCreateTime() < sessionTimeout){
                return;
            }
            else {
                //超时，通知对方关闭连接(如果处理不过来，会发送多次的)
                value.closechannel(new IoUpChannelClose(true, "session timeout"));
            }
        });
        removeList.forEach( key-> unLoginChannels.remove(key));
    }

    private class SessionCheck{}

    private static class ChannelUnLogin {
        private final long createTime;
        private final ChannelInteractor decorator;

        public ChannelUnLogin(ChannelInteractor decorator) {
            this.decorator = decorator;
            this.createTime = System.currentTimeMillis();
        }

        public boolean isLogin() {
            ChannelSession session = decorator.getSession();
            return session != null && session.isLogin();
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getUniqueId() {
            return decorator.getUniqueId();
        }

        public void closechannel(IoUpChannelClose channelClose){
            decorator.receiveIoMessage(channelClose);
        }

        public boolean isClose(){
            ChannelSession session = decorator.getSession();
            return (session != null && session.isDispose()) || decorator.isClose();
        }
    }
}
