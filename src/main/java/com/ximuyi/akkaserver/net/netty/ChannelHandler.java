package com.ximuyi.akkaserver.net.netty;

import akka.actor.ActorRef;
import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.io.IoUpChannelClose;
import com.ximuyi.akkaserver.message.MsChannelNew;
import com.ximuyi.akkaserver.session.channel.ChannelInteractor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        if (logger.isDebugEnabled()) {
            logger.debug("Accept channel from IP[{}]", NettyUtil.getIpByNettyChannel(channel));
        }
        boolean add = NettyChannelGroup.instance.add(channel);
        if (add){
            ChannelInteractor decorator = new ChannelInteractor(channel, Thread.currentThread().getId());
            AttrManager.initChannel(channel, decorator);
            ActorRef session = ContextResolver.getSessionActor();
            session.tell(new MsChannelNew(decorator), ActorRef.noSender());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        // 由于有时候channel close不是后端触发，是因为网络原因，一般是前端断开连接，导致channel close了，
        // 这时就需要走Disconnect流程了。
        notifyAkkaUserClose(channel, false,"inactive channel");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        // 当ChannelOutboundBuffer的写缓冲区大小超过高水位(io.netty.channel.ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK，默认64K)时，isWritable变为false；
        // 写缓冲区超过高水位时，会自动扩容为原来的2倍，后面再超会再扩容，每次都是翻倍扩容。
        // 因为写缓冲区会无限扩容，为防止OOM，一旦超过高水位，就断开连接，让客户端走断线重连流程。
        Channel channel = ctx.channel();
        if (!channel.isWritable()) {
            notifyAkkaUserClose(channel, false,"writability changed");
        }
    }

    /***
     * @param channel
     */
    public static void notifyAkkaUserClose(Channel channel, boolean notifyClient, String reason){
        ChannelInteractor session = AttrManager.getSession(channel);
        session.receiveIoMessage(new IoUpChannelClose(notifyClient, reason));
    }
}
