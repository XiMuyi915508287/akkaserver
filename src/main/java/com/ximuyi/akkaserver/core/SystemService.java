package com.ximuyi.akkaserver.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.actor.AkkaActor;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.message.MsConfig;
import com.ximuyi.akkaserver.session.AkkaUserService;
import com.ximuyi.akkaserver.session.SessionService;

public class SystemService extends AkkaActor {

    private final ActorSystem system;

    public SystemService(ActorSystem system) {
        this.system = system;
    }

    @Override
    public void init(ConfigWrapper config) throws Throwable {
        MsConfig messageInit = new MsConfig(config);
        AkkaMediator mediator = ContextResolver.getComponent(AkkaMediator.class);

        ActorRef sessionService = system.actorOf(Props.create(SessionService.class), SessionService.IDENTIFY);
        mediator.setSessionActor(sessionService);
        this.getContext().watch(sessionService);
        sessionService.tell(messageInit, getSelf());

        ActorRef akkaUserService = system.actorOf(Props.create(AkkaUserService.class), AkkaUserService.IDENTIFY);
        mediator.setUserActor(akkaUserService);
        this.getContext().watch(akkaUserService);
        akkaUserService.tell(messageInit, getSelf());
    }

    @Override
    protected void buildAkkaReceive(ReceiveBuilder builder) {
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }
}
