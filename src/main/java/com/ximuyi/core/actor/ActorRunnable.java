package com.ximuyi.core.actor;

import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorRunnable implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ActorRunnable.class);

    private final String name;
    private final Runnable runnable;
    private final ActorRef actorRef;

    public ActorRunnable(ActorRef actorRef, String name, Runnable runnable) {
        this.actorRef = actorRef;
        this.name = name;
        this.runnable = runnable;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        }
        catch (Throwable t){
            logger.error("actor:{} name:{} runnable executed failure.", actorRef == null ? "null" : actorRef.path(), name, t);
        }
    }
}
