package com.ximuyi.core.command;

import io.netty.buffer.ByteBuf;

public interface ICommand {

    public int getUniqueId();

    public void write(ByteBuf byteBuf);
}
