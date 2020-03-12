package com.ximuyi.core.net.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyChannelGroup extends DefaultChannelGroup {
    public static NettyChannelGroup instance = new NettyChannelGroup();

    private NettyChannelGroup() {
        super(GlobalEventExecutor.INSTANCE);
    }

    @Override
    public boolean add(Channel channel) {
        boolean result = super.add(channel);
        return result;
    }

    @Override
    public boolean remove(Object o) {
        // 因为add进来的channel会监听channel.closeFuture事件，
        // 在channel close的时候自动调remove，所以不用手动调remove。
        return super.remove(o);
    }
}


