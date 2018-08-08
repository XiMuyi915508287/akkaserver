package com.ximuyi.akkaserver.extension;

public class CommandConst {
    public static final ICommand LOGIN = new Command((short) 0, (byte) 1);
    public static final ICommand LOGOUT = new Command((short) 0, (byte) 2);
    public static final ICommand CHANNEL_CLOSE = new Command((short) 0, (byte) 3);
    public static final ICommand HEART_BEAT = new Command((short) 0, (byte) 4);

}
