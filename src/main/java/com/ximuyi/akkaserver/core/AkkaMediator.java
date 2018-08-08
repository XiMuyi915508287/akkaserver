package com.ximuyi.akkaserver.core;

import akka.actor.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.ximuyi.akkaserver.actor.AkkaDeadLetter;
import com.ximuyi.akkaserver.component.Component;
import com.ximuyi.akkaserver.component.ReceiveAction;
import com.ximuyi.akkaserver.config.ConfigKey;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.message.MsConfig;
import com.ximuyi.akkaserver.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class AkkaMediator extends Component {

    private static Logger logger = LoggerFactory.getLogger(AkkaMediator.class);

    //系统Akka
    private ActorSystem system;
    //系统邮箱
    private Inbox systemInbox;

    private ActorRef systemActor;
    private ActorRef sessionActor;
    private ActorRef userActor;


    public AkkaMediator() {
        super(AkkaMediator.class.getName());
    }

    @Override
    public void init(ConfigWrapper config) throws Throwable {
        super.init(config);
        String akkaSystem = config.getString(ConfigKey.AKKA_SYSTEM, "system");
        File file = FileUtil.scanFileByPath(config.getRoot(), "akka.conf");
        Config cg = ConfigFactory.parseFile(file);
        cg.withFallback(ConfigFactory.defaultReference(Thread.currentThread().getContextClassLoader()));
        Config akkaConfig = ConfigFactory.load(cg);
        system = ActorSystem.create(akkaSystem, akkaConfig);
        systemInbox = Inbox.create(system);

        String akkaName = config.getString(ConfigKey.AKKA_NAME, "akka");
        systemActor = system.actorOf(Props.create(SystemService.class, system), akkaName);
        systemInbox.send(systemActor, new MsConfig(config));

        ActorRef deadLetterListener = system.actorOf(Props.create(AkkaDeadLetter.class));
        system.eventStream().subscribe(deadLetterListener, DeadLetter.class);
    }

    public Inbox getSystemInbox() {
        return systemInbox;
    }

    public ActorSystem getSystem() {
        return system;
    }

    public ActorRef getSessionActor() {
        return sessionActor;
    }

    public void setSessionActor(ActorRef sessionActor) {
        this.sessionActor = sessionActor;
    }

    public ActorRef getUserActor() {
        return userActor;
    }

    public void setUserActor(ActorRef userActor) {
        this.userActor = userActor;
    }

    @Override
    protected void buildSycReceive(ReceiveAction receive) {

    }
}
