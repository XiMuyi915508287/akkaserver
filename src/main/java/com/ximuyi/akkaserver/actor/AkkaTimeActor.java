package com.ximuyi.akkaserver.actor;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Inbox;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.message.MsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AkkaTimeActor extends AbstractActorWithTimers {

    private static Logger logger = LoggerFactory.getLogger(AkkaActor.class);

    protected final void sendWithBox(ActorRef receive, Object message){
        Inbox inbox = ContextResolver.getSystemInbox();
        inbox.send(receive, message);
    }

    @Override
    public final Receive createReceive() {
        ReceiveBuilder builder = receiveBuilder();
        builder.match(MsConfig.class, (messageInit -> {
            try {
                init(messageInit.getWrapper());
            } catch (Throwable throwable) {
                logger.error("{} init error.", getName(), throwable);
            }
        }));

        buildAkkaReceive(builder);
        return builder.build();
    }

    public String getName(){
        return this.getClass().getName();
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
        logger.error("{} unhandled objet：{}" + getName(), message.toString());
    }

    public abstract void init(ConfigWrapper config) throws Throwable;

    protected abstract void buildAkkaReceive(ReceiveBuilder builder);
}
