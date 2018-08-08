package com.ximuyi.akkaserver.io;

import com.ximuyi.akkaserver.extension.ICommand;
import io.netty.buffer.ByteBuf;

public abstract class IoDownPackage {
    private final ICommand cmd;

    public IoDownPackage(ICommand cmd) {
        this.cmd = cmd;
    }

    public ICommand getCmd() {
        return cmd;
    }

    public abstract ByteBuf toByteBuf();
}
