package com.ximuyi.core.net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ChannelHandler.Sharable
public class ByteBufDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(ByteBufDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int dataLength = msg.readInt();
        msg.markReaderIndex().resetReaderIndex();
        out.add(msg);
        //+1避免被回收掉，让下一个使用
        ReferenceCountUtil.retain(msg);
    }
}
