package com.ximuyi.akkaserver.message;


import com.ximuyi.akkaserver.session.channel.ChannelSession;

public class MsChannelClose {

    private final ChannelSession session;

    public MsChannelClose(ChannelSession session) {
        this.session = session;
    }

    public ChannelSession getSession() {
        return session;
    }
}
