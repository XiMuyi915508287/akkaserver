package com.ximuyi.akkaserver.session;

public interface IAkkaUserConfig {
    long idleTimeOut();
    long idleCheckTime();
}
