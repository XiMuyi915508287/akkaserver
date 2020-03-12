package com.ximuyi.core.net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteBuffer;

@ChannelHandler.Sharable
public class ByteBufEncoder extends MessageToByteEncoder<ByteBuffer> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuffer msg, ByteBuf out) throws Exception {
        out.writeBytes(msg);
    }
}
