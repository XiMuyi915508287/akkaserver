package com.ximuyi.akkaserver.session;

import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.actor.AkkaActor;
import com.ximuyi.akkaserver.config.ConfigWrapper;

public class AkkaUserService extends AkkaActor {

    public static final String IDENTIFY = "user";

    @Override
    public void init(ConfigWrapper config) throws Throwable {

    }

    @Override
    protected void buildAkkaReceive(ReceiveBuilder builder) {

    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }
}
