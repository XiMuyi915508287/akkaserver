package com.ximuyi.core.net.netty;

import akka.actor.ActorRef;
import com.ximuyi.core.actor.message.MsChannelClose;
import com.ximuyi.core.actor.message.MsChannelOpen;
import com.ximuyi.core.ContextResolver;
import com.ximuyi.core.session.SessionManager;
import com.ximuyi.core.session.UserSession;
import com.ximuyi.core.session.channel.ChannelCloseReason;
import com.ximuyi.core.session.channel.ChannelUtil;
import com.ximuyi.core.session.channel.NetChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
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
            NetChannel netChannel = new NetChannel(channel);
            AttrManager.initChannel(channel, netChannel);
            ActorRef session = ContextResolver.getSessionActor();
            session.tell(new MsChannelOpen(netChannel), ActorRef.noSender());
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
        notifySessionClose(channel, false,ChannelCloseReason.INACTIVE);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        // 当ChannelOutboundBuffer的写缓冲区大小超过高水位(io.netty.channel.ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK，默认64K)时，isWritable变为false；
        // 写缓冲区超过高水位时，会自动扩容为原来的2倍，后面再超会再扩容，每次都是翻倍扩容。
        // 因为写缓冲区会无限扩容，为防止OOM，一旦超过高水位，就断开连接，让客户端走断线重连流程。
        Channel channel = ctx.channel();
        if (!channel.isWritable()) {
            notifySessionClose(channel, false,ChannelCloseReason.WRITE_VARIABLE);
        }
    }

    /***
     * @param channel
     */
    public static void notifySessionClose(Channel channel, boolean notifyClient, String reason){
        NetChannel netChannel = AttrManager.getSession(channel);
        UserSession session = SessionManager.getInstance().getUserSession(netChannel.getBindUserId());
        if (session != null){
            session.tell(new MsChannelClose(reason, netChannel.getUniqueId()), ActorRef.noSender());
        }
        else {
            // 如果同步问题导致找不到session，就等心跳检测断线吧~
            ChannelUtil.channelClose(netChannel, reason, notifyClient);
        }
    }
}
