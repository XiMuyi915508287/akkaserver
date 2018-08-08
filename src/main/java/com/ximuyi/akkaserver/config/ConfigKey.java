package com.ximuyi.akkaserver.config;

public class ConfigKey {
    private static final String ROOT = "com.ximuyi.akkaserver";

    private static final String build(String key){
        return ROOT + "." + key;
    }

    /****************************配置如下*******************************************/

    public static final String SERVER_APP_LITENER = build("server.applistener");
    public static final String SERVER_LOGIN_HELPER = build("server.loginhelper");
    public static final String SERVER_SCHEDULE = build("server.schedule");
    public static final String SERVER_SCODER = build("server.coder");

    public static final String AKKA_SYSTEM = build("akka.system");
    public static final String AKKA_NAME = build("akka.name");

    public static final String AKKA_SESSION_TIMEOUT = build("akka.session.timeout");
    public static final String AKKA_SESSION_CHECKTIME = build("akka.session.checktime");

    public static final String AKKA_USER_TIMEOUT = build("akka.user.timeout");
    public static final String AKKA_USER_CHECKTIME = build("akka.user.checktime");

    public static final String NETTY_BOSS_THREAD = build("netty.bossthread");
    public static final String NETTY_CHILD_THREAD = build("netty.childthread");
    public static final String NETTY_LISTEN_PORT = build("netty.listenport");
}
