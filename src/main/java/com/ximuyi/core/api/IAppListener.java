package com.ximuyi.core.api;

public interface IAppListener {
    void onInit() throws Throwable;
    void onLaunch();
    void onShutdown();
}
