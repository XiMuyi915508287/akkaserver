package com.ximuyi.core.net.netty;

import com.ximuyi.core.session.channel.NetChannel;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class AttrManager {

    private static final AttributeKey<NetChannel> channelKey = AttributeKey.newInstance("channelKey");
    private static final AttributeKey<Boolean> disposeKey = AttributeKey.newInstance("disposeKey");

    static void initChannel(Channel channel, NetChannel session) {
        channel.attr(disposeKey).set(false);
        channel.attr(channelKey).set(session);
    }

    public static NetChannel getSession(Channel channel){
        return channel.attr(channelKey).get();
    }

    public static boolean doDisposeChannel(Channel channel) {
        return channel.attr(disposeKey).compareAndSet(false, true);
    }

    public static boolean isDisposed(Channel channel) {
        Boolean res = channel.attr(disposeKey).get();
        return res != null && res;
    }
}
