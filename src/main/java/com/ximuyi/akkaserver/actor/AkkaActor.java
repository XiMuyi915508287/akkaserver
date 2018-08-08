package com.ximuyi.akkaserver.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Inbox;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.message.MsConfig;
import com.ximuyi.akkaserver.message.MsRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AkkaActor extends AbstractActor {

    private static Logger logger = LoggerFactory.getLogger(AkkaActor.class);

    public AkkaActor() {
    }

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
        builder.match(Terminated.class, terminated -> {
            ActorRef actorRef = terminated.actor();
            onTerminate(actorRef);
        });
        builder.match(MsRunnable.class, msAkkaRunnable -> msAkkaRunnable.handle());
        buildAkkaReceive(builder);
        return builder.build();
    }

    public String getName(){
        return this.getClass().getName();
    }

    protected void onTerminate(ActorRef actorRef){
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
        logger.error("{} unhandled objet：{}" + getName(), message.toString());
    }

    public void init(ConfigWrapper config) throws Throwable{

    }

    protected abstract void buildAkkaReceive(ReceiveBuilder builder);
}
