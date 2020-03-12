package com.ximuyi.core.config;

public class ConfigKey {
    private static final String ROOT = "com.ximuyi.core";

    private static final String build(String key){
        return ROOT + "." + key;
    }

    /****************************配置如下*******************************************/

    public static final String SERVER_APP_LISTENER = build("server.app.listener");
    public static final String SERVER_LOGIN_HELPER = build("server.login.helper");
    public static final String SERVER_SCHEDULE = build("server.schedule");
    public static final String SERVER_CODER = build("server.coder");

    public static final String AKKA_SYSTEM = build("akka.system");
    public static final String AKKA_NAME = build("akka.name");

    public static final String AKKA_SESSION_TIMEOUT = build("akka.session.timeout");
    public static final String AKKA_SESSION_TICK = build("akka.session.tick");

    public static final String AKKA_USER_TIMEOUT = build("akka.user.timeout");
    public static final String AKKA_USER_TICK = build("akka.user.tick");

    public static final String NETTY_BOSS_THREAD = build("netty.boss.thread");
    public static final String NETTY_CHILD_THREAD = build("netty.child.thread");
    public static final String NETTY_LISTEN_PORT = build("netty.listen.port");
}
