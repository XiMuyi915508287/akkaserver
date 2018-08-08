package com.ximuyi.akkaserver.session;

import akka.actor.ActorRef;
import com.ximuyi.akkaserver.IAkkaUser;
import com.ximuyi.akkaserver.io.IoDownPackage;

public interface ISession{

    boolean sendIoMessage(IoDownPackage message);

    IAkkaUser getAkkaUser();

    boolean isLogin();

    void tell(Object message, ActorRef sender);

    ActorRef getSelf();
}
