package com.ximuyi.core.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ximuyi.core.actor.AkkaActor;
import com.ximuyi.core.session.SessionService;

public class SystemService extends AkkaActor {

    public SystemService(ActorSystem system) {

        AkkaMediator mediator = ContextResolver.getComponent(AkkaMediator.class);

        ActorRef sessionService = system.actorOf(Props.create(SessionService.class), SessionService.IDENTIFY);
        mediator.setSessionRef(sessionService);
        this.getContext().watch(sessionService);
    }
}
