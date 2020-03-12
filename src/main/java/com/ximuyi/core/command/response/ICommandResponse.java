package com.ximuyi.core.command.response;

import com.ximuyi.core.command.ICommand;
import io.netty.buffer.ByteBuf;

public interface ICommandResponse {

    ICommand getCommand();

    long getUniqueId();

    ByteBuf toByteBuf();
}
