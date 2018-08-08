package com.ximuyi.akkaserver.api;

import com.ximuyi.akkaserver.config.ConfigWrapper;

public interface AppListener {
    void onInit(ConfigWrapper config) throws Throwable;
    void onLaunch();
    void onShutdown();
}
