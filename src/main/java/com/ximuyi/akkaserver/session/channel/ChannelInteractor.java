package com.ximuyi.akkaserver.session.channel;

import akka.actor.ActorRef;
import com.ximuyi.akkaserver.io.IoDownPackage;
import com.ximuyi.akkaserver.io.IoUpMessage;
import com.ximuyi.akkaserver.net.netty.AttrManager;
import com.ximuyi.akkaserver.net.netty.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;


public class ChannelInteractor {

    private static final Logger logger = LoggerFactory.getLogger(ChannelInteractor.class);

    private static final AtomicLong ChannelId = new AtomicLong(0);

    private final Channel channel;
    private final long threadId;
    private final String addressIp;
    private final long uniqueId;
    private volatile ChannelSession session;
    //缓存一部分信息
    private Deque<IoUpMessage> cacheUpMessages = new LinkedList<>();

    public ChannelInteractor(Channel channel, long threadId) {
        this.channel = channel;
        this.threadId = threadId;
        this.addressIp = NettyUtil.getIpByNettyChannel(channel);
        this.uniqueId = ChannelId.incrementAndGet();
    }

    public boolean sendIoMessage(IoDownPackage message) {
        if (!channel.isWritable() || !channel.isActive()  || AttrManager.isDisposed(channel)){
            return false;
        }
        channel.eventLoop().execute(() -> {
            // writeAndFlush进入eventLoop队列时，channel是正常的，
            // 但是execute的时候，channel状态可能已经发生变化，所以再判断一次。
            // 虽然可以等netty write的时候抛异常再处理异常，但是，还是提前判断一下，严谨一点。
            if (!channel.isWritable() || !channel.isActive()  || AttrManager.isDisposed(channel)){
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
        });
        return true;
    }

    public void receiveIoMessage(IoUpMessage message) {
        if (Thread.currentThread().getId() == threadId) {
            onReceiveIoMessage(message);
        } else {
            channel.eventLoop().execute(() -> onReceiveIoMessage(message));
        }
    }

    private void onReceiveIoMessage(IoUpMessage message){
        if (Thread.currentThread().getId() != threadId){
            throw new UnsupportedOperationException("线程问题");
        }
        if (session == null){
            cacheUpMessages.add(message);
            return;
        }
        while (!cacheUpMessages.isEmpty()){
            IoUpMessage msg = cacheUpMessages.pollFirst();
            session.tell(msg, ActorRef.noSender());
        }
        session.tell(message, ActorRef.noSender());
    }

    public String getIpAddress() {
        return addressIp;
    }

    public boolean isClose(){
        return AttrManager.isDisposed(channel);
    }

    public boolean disconnect(String reason) {
        String addressIp = NettyUtil.getIpByNettyChannel(channel);
        boolean success = AttrManager.doDisposeChannel(channel);
        if (success) {
            channel.close().addListener(future -> {
                if (future.isSuccess()) {
                    logger.debug("close channel success, channelId:{}, ip[{}] removed, reason[{}] success.", uniqueId, addressIp, reason);
                } else {
                    logger.debug("close channel failed, channelId:{}, ip[{}] remove error! remove reason[{}]", uniqueId, addressIp, reason, future.cause());
                }
            });
        }
        return success;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public ChannelSession getSession(){
        return session;
    }

    public void setSession(ChannelSession session) {
        this.session = session;
    }
}
