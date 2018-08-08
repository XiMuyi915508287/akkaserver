package com.ximuyi.akkaserver.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsRunnable {

    private static final Logger logger = LoggerFactory.getLogger(MsRunnable.class);

    private final Runnable runnable;

    public MsRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void handle() {
        try {
            runnable.run();
        }
        catch (Throwable t){
            logger.error("", t);
        }
    }
}
