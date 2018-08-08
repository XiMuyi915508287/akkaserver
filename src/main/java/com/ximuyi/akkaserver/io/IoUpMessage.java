package com.ximuyi.akkaserver.io;

import com.ximuyi.akkaserver.extension.ICommand;

public class IoUpMessage {
    private final ICommand cmd;
    private final Object message;

    public IoUpMessage(ICommand cmd, Object message) {
        this.cmd = cmd;
        this.message = message;
    }

    public ICommand getCmd() {
        return cmd;
    }

    public Object getMessage() {
        return message;
    }
}
