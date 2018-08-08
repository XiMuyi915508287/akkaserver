package com.ximuyi.akkaserver.message;

import com.ximuyi.akkaserver.config.ConfigWrapper;

public class MsConfig {

    private final ConfigWrapper wrapper;

    public MsConfig(ConfigWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public ConfigWrapper getWrapper() {
        return wrapper;
    }
}
