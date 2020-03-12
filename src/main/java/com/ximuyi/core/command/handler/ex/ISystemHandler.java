package com.ximuyi.core.command.handler.ex;

import io.netty.buffer.ByteBuf;

public interface ISystemHandler {
    Object decode(ByteBuf byteBuf);
}
