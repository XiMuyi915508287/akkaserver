package com.ximuyi.core.session.channel;

public class ChannelCloseReason {
    public static final String LOGOUT = "登出";
    public static final String LOGIN_TIME_OUT = "登陆超时";
    public static final String HEART_BEAT = "心跳异常";
    public static final String LOGIN_AGAIN = "账号在别处登陆";
    public static final String WRITE_VARIABLE = "Channel写问题";
    public static final String DECODE_ERROR = "协议包异常";
    public static final String INACTIVE = "网络断开";
}
