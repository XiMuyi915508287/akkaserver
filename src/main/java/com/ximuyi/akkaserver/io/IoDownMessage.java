package com.ximuyi.akkaserver.io;

import com.ximuyi.akkaserver.coder.MessageCoderUtil;
import com.ximuyi.akkaserver.coder.ResponseCode;
import com.ximuyi.akkaserver.extension.ICommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class IoDownMessage extends IoDownPackage {

    private static final int INT_LENGTH = 4;

    private final Object message;
    private final short resultCode;

    public IoDownMessage(ICommand cmd, short resultCode, Object message) {
        super(cmd);
        this.message = message;
        this.resultCode = resultCode;
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        byteBuf.writeInt(0);
        getCmd().wirte(byteBuf);
        byteBuf.writeShort(resultCode);
        if (message != null){
            byte[] bytes = MessageCoderUtil.encode(getCmd(), message);
            if (bytes != null){
                byteBuf.writeBytes(bytes);
            }
            else {
                byteBuf.setShort(INT_LENGTH, ResponseCode.ENCODE_EXCEPTION);
            }
        }
        int length = byteBuf.readableBytes();
        byteBuf.setInt(0,  length - INT_LENGTH);
        return byteBuf;
    }
}
