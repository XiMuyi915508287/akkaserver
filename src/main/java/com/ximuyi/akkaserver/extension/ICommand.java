package com.ximuyi.akkaserver.extension;

import io.netty.buffer.ByteBuf;

public interface ICommand {

    public int getUniqueId();

    public void wirte(ByteBuf byteBuf);
}
