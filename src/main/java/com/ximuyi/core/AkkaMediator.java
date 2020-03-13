package com.ximuyi.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.ximuyi.core.actor.AkkaDeadLetter;
import com.ximuyi.core.component.Component;
import com.ximuyi.core.config.ConfigKey;
import com.ximuyi.core.config.Configs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AkkaMediator extends Component {

    private static Logger logger = LoggerFactory.getLogger(AkkaMediator.class);

    //系统Akka
    private ActorSystem system;
    private ActorRef systemRef;
    private ActorRef sessionRef;


    public AkkaMediator() {
        super(AkkaMediator.class.getName());
    }

    @Override
    public void init() throws Throwable {
        Configs configs = Configs.getInstance();
        String akkaSystem = configs.getString(ConfigKey.AKKA_SYSTEM, "system");
        Config config = ConfigFactory.load("akka");
        config.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config akkaConfig = ConfigFactory.load(config);
        system = ActorSystem.create(akkaSystem, akkaConfig);

        String akkaName = configs.getString(ConfigKey.AKKA_NAME, "akka");
        systemRef = system.actorOf(Props.create(SystemService.class, system), akkaName);

        ActorRef deadLetterListener = system.actorOf(Props.create(AkkaDeadLetter.class));
        system.eventStream().subscribe(deadLetterListener, DeadLetter.class);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public ActorSystem getSystem() {
        return system;
    }

    public ActorRef getSessionRef() {
        return sessionRef;
    }

    public void setSessionRef(ActorRef sessionRef) {
        this.sessionRef = sessionRef;
    }
}
