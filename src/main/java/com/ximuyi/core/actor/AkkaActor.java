package com.ximuyi.core.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AkkaActor extends AbstractActor {

    private static Logger logger = LoggerFactory.getLogger(AkkaActor.class);

    public AkkaActor() {
    }

    @Override
    public final Receive createReceive() {
        ReceiveBuilder builder = receiveBuilder();
        builder.match(Terminated.class, terminated -> {
            ActorRef actorRef = terminated.actor();
            onTerminate(actorRef);
        });
        onReceiveBuilder(builder);
        return builder.build();
    }

    public String getName(){
        return this.getClass().getName();
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
        ActorRef sender = getSender();
        logger.error("actor:{} unhandled message：{}, sender:{}", getSelf().path(), message, sender == null ? "null" : sender.path().name());
    }

    protected void onTerminate(ActorRef actorRef){

    }

    protected void onReceiveBuilder(ReceiveBuilder builder){

    }
}
