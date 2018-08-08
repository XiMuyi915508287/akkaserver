package com.ximuyi.akkaserver.session;

import akka.actor.ActorRef;

public class AkkaUserCreate {
    private final boolean isOld;
    private final ActorRef akkaUser;

    public AkkaUserCreate(boolean isOld, ActorRef akkaUser) {
        this.isOld = isOld;
        this.akkaUser = akkaUser;
    }

    public boolean isOld() {
        return isOld;
    }

    public ActorRef getAkkaUser() {
        return akkaUser;
    }
}
