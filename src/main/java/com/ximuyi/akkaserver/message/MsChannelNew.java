package com.ximuyi.akkaserver.message;


import com.ximuyi.akkaserver.session.channel.ChannelInteractor;

public class MsChannelNew {

    private final ChannelInteractor decorator;

    public MsChannelNew(ChannelInteractor decorator) {
        this.decorator = decorator;
    }

    public ChannelInteractor getDecorator() {
        return decorator;
    }
}
