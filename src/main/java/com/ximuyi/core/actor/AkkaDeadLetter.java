package com.ximuyi.core.actor;

import akka.actor.DeadLetter;
import akka.japi.pf.ReceiveBuilder;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.actor.message.MsCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AkkaDeadLetter extends AkkaActor {

    private static final Logger logger = LoggerFactory.getLogger(AkkaDeadLetter.class);

    @Override
    protected void onReceiveBuilder(ReceiveBuilder builder) {
        builder.match(DeadLetter.class, letter->{
            if (letter.message() != null && letter.message() instanceof MsCallback){
                ((MsCallback)letter.message()).onCallback(ResultCode.AKKA_DEATH);
            }
        });
    }
}
