package com.ximuyi.akkaserver.actor;

import akka.actor.DeadLetter;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.akkaserver.coder.ResponseCode;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.message.MsCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AkkaDeadLetter extends AkkaActor {

    private static final Logger logger = LoggerFactory.getLogger(AkkaDeadLetter.class);

    @Override
    public void init(ConfigWrapper config) throws Throwable {

    }

    @Override
    protected void buildAkkaReceive(ReceiveBuilder builder) {
        builder.match(DeadLetter.class, letter->{
            if (letter.message() != null && letter.message() instanceof MsCallback){
                ((MsCallback)letter.message()).onCallback(ResponseCode.AKKA_DEATH);
            }
        });
    }
}
