package com.ximuyi.akkaserver.net.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("sessionHandler", new ChannelHandler());
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024 * 5, 0, 4, 0, 0));
        pipeline.addLast("decoderHandler", new ByteBufDecoder());
        pipeline.addLast("inputMsgHandler", new InputMsgHandler());
        pipeline.addLast("encoderHandler", new ByteBufEncoder());
        //全局异常处理，这个handler要放到最后。
        pipeline.addLast("exceptionHandler", new ExceptionHandler());
    }
}
