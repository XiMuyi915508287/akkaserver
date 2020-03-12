package com.ximuyi.core.coder;

import java.io.IOException;

import com.ximuyi.core.command.ICommand;
import io.netty.buffer.ByteBuf;

public interface IMessageCoder<T> {

    byte[] encode(ICommand extCmd, T obj) throws IOException;

    T decode(ICommand command, ByteBuf buffer) throws IOException;

    T empty();

    T encodeString(String string) throws IOException;

    T encodeInt(int value) throws IOException;

    T encodeLong(long value) throws IOException;

    String byteString(ByteBuf buffer) throws IOException;

    int byteInt(ByteBuf buffer) throws IOException;

    long byteLong(ByteBuf buffer) throws IOException;
}
