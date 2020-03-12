package com.ximuyi.core.actor.message;


public class MsChannelClose {

    private final String reason;
    private final long channelUniqueId;

    public MsChannelClose(String reason, long channelUniqueId) {
        this.reason = reason;
        this.channelUniqueId = channelUniqueId;
    }

    public String getReason() {
        return reason;
    }

    public long getChannelUniqueId() {
        return channelUniqueId;
    }
}
