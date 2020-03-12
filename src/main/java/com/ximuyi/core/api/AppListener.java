package com.ximuyi.core.api;

public interface AppListener {
    void onInit() throws Throwable;
    void onLaunch();
    void onShutdown();
}
