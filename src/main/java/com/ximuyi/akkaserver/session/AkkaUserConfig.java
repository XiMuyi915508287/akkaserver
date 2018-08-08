package com.ximuyi.akkaserver.session;

import com.ximuyi.akkaserver.config.ConfigKey;
import com.ximuyi.akkaserver.config.ConfigWrapper;

public class AkkaUserConfig implements IAkkaUserConfig {

    public static final AkkaUserConfig instance = new AkkaUserConfig();

    @Override
    public long idleTimeOut() {
        return ConfigWrapper.instance().getInteger(ConfigKey.AKKA_USER_TIMEOUT, 30) * 1000L;
    }

    @Override
    public long idleCheckTime() {
        return ConfigWrapper.instance().getInteger(ConfigKey.AKKA_USER_CHECKTIME, 5) * 1000L;
    }
}
