package com.ximuyi.akkaserver.io;

import com.ximuyi.akkaserver.extension.ICommand;
import io.netty.buffer.ByteBuf;

public class IoDownShared extends IoDownPackage {

    private final ByteBuf original;

    public IoDownShared(ICommand cmd, ByteBuf original) {
        super(cmd);
        this.original = original;
    }

    @Override
    public ByteBuf toByteBuf() {
        //original初始化为1, duplicate 不会+1, retain+1
        return original.duplicate().retain();
    }
}
