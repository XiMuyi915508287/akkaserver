package com.ximuyi.akkaserver.net.netty;

import com.ximuyi.akkaserver.session.channel.ChannelInteractor;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class AttrManager {
    private static final AttributeKey<ChannelInteractor> sessionKey = AttributeKey.newInstance("sessionKey");
    private static final AttributeKey<Boolean> disposeKey = AttributeKey.newInstance("disposeKey");

    static void initChannel(Channel channel, ChannelInteractor session) {
        channel.attr(disposeKey).set(false);
        channel.attr(sessionKey).set(session);
    }

    public static ChannelInteractor getSession(Channel channel){
        return channel.attr(sessionKey).get();
    }

    public static boolean doDisposeChannel(Channel channel) {
        return channel.attr(disposeKey).compareAndSet(false, true);
    }

    public static boolean isDisposed(Channel channel) {
        Boolean res = channel.attr(disposeKey).get();
        return res != null && res;
    }
}
