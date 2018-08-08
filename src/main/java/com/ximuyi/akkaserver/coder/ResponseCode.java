package com.ximuyi.akkaserver.coder;

public class ResponseCode {
    public static final short SUCCESS = 0;
    public static final short UNKNOW = -1;
    public static final short INVALID = -2;
    public static final short ENCODE_EXCEPTION = -3;
    public static final short LOGIN_ALREADY = -4;
    public static final short SEVER_EXCEPTION = -5;
    public static final short RETRY_LOGIN = -6;
    public static final short AKKA_DEATH = -7;
}
