package com.ximuyi.core.actor.message;


import com.ximuyi.core.session.channel.NetChannel;

public class MsChannelOpen {

    private final NetChannel channel;

    public MsChannelOpen(NetChannel channel) {
        this.channel = channel;
    }

    public NetChannel getChannel() {
        return channel;
    }
}
