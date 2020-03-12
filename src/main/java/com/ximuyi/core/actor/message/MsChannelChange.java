package com.ximuyi.core.actor.message;


import com.ximuyi.core.api.login.ConnectWay;
import com.ximuyi.core.session.channel.NetChannel;

public class MsChannelChange {

    private final NetChannel channel;
    private final ConnectWay connectWay;

    public MsChannelChange(NetChannel channel, ConnectWay connectWay) {
        this.channel = channel;
        this.connectWay = connectWay;
    }

    public NetChannel getChannel() {
        return channel;
    }

    public ConnectWay getConnectWay() {
        return connectWay;
    }
}
