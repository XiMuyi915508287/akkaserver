package com.ximuyi.core.command;

public class CommandUtil {

    public static final ICommand LOGIN = new Command((short) 0, (byte) 1);
    public static final ICommand LOGOUT = new Command((short) 0, (byte) 2);
    public static final ICommand CHANNEL_CLOSE = new Command((short) 0, (byte) 3);
    public static final ICommand HEART_BEAT = new Command((short) 0, (byte) 4);
    public static final ICommand CACHE_RESPONSE = new Command((short) 0, (byte) 5);

    public static final boolean equals(ICommand command0, ICommand command1){
        return command0.getUniqueId() == command1.getUniqueId();
    }
}
