package com.ximuyi.core.session.channel;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.ximuyi.core.command.response.CmdChannelClose;
import com.ximuyi.core.command.response.ICommandResponse;
import com.ximuyi.core.net.netty.AttrManager;
import com.ximuyi.core.net.netty.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 修改状态只能在AkkaUser线程中
 */
public class NetChannel {

    private static final Logger logger = LoggerFactory.getLogger(NetChannel.class);

    private static final AtomicLong CHANNEL_ID_GEN = new AtomicLong(0);

    private final Channel channel;
    private final long channelUniqueId;
    private final String addressIp;
    private volatile long bindUserId = 0;
    private volatile boolean bindClosed = false;

    public NetChannel(Channel channel) {
        this.channel = channel;
        this.channelUniqueId = CHANNEL_ID_GEN.incrementAndGet();
        this.addressIp = NettyUtil.getIpByNettyChannel(channel);
    }

    public boolean addCommandResponse(ICommandResponse message) {
        if (isUnWritable()){
            return false;
        }
        channel.eventLoop().execute(() -> onCommandResponse(message));
        return true;
    }

    public void scheduleClose(CmdChannelClose message, long delay, TimeUnit timeUnit) {
        addCommandResponse(message);
        channel.eventLoop().schedule(() -> disConnect(message.getReason()), delay, timeUnit);
    }

    private void onCommandResponse(ICommandResponse message){
        // writeAndFlush进入eventLoop队列时，channel是正常的，
        // 但是execute的时候，channel状态可能已经发生变化，所以再判断一次。
        // 虽然可以等netty write的时候抛异常再处理异常，但是，还是提前判断一下，严谨一点。
        if (isUnWritable()){
            return;
        }
        ByteBuf byteBuf = message.toByteBuf();
        channel.writeAndFlush(byteBuf).addListener(future -> {
            if (future.isSuccess()) {
            }
            else {
                logger.error("channel[{}] address[{}] write error.", getUniqueId(), getIpAddress(), future.cause());
            }
        });
    }

    public boolean isUnWritable(){
        return !channel.isWritable() || !channel.isActive()  || AttrManager.isDisposed(channel);
    }

    public String getIpAddress() {
        return addressIp;
    }

    public boolean isClosed(){
        return AttrManager.isDisposed(channel);
    }

    public boolean disConnect(String reason) {
        String addressIp = NettyUtil.getIpByNettyChannel(channel);
        boolean success = AttrManager.doDisposeChannel(channel);
        if (success) {
            channel.close().addListener(future -> {
                if (future.isSuccess()) {
                    logger.debug("close channel success, channelId:{}, ip[{}] removed, reason[{}] success.", channelUniqueId, addressIp, reason);
                } else {
                    logger.debug("close channel failed, channelId:{}, ip[{}] remove error! remove reason[{}]", channelUniqueId, addressIp, reason, future.cause());
                }
            });
        }
        return success;
    }

    public long getUniqueId() {
        return channelUniqueId;
    }

    public long getBindUserId() {
        return bindUserId;
    }

    public void setBindUserId(long bindUserId) {
        this.bindUserId = bindUserId;
    }

    public void setBindClosed() {
        this.bindClosed = true;
    }

    public boolean isBindClosed() {
        return bindClosed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetChannel that = (NetChannel) o;
        return channelUniqueId == that.channelUniqueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelUniqueId);
    }
}
