package com.ximuyi.core.net.netty;

import com.ximuyi.core.session.channel.NetChannel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NetChannel session = AttrManager.getSession(ctx.channel());
        logger.error("channel[{}] address[{}] exception:", session.getUniqueId(), session.getIpAddress(), cause);
    }
}
