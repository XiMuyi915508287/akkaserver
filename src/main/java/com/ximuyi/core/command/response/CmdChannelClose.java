package com.ximuyi.core.command.response;

import com.ximuyi.core.coder.MessageCoderUtil;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.CommandUtil;

public class CmdChannelClose extends ByteResponse {

    private final String reason;

    public CmdChannelClose(String reason) {
        super(CommandUtil.CHANNEL_CLOSE, ResultCode.SUCCESS, MessageCoderUtil.encodeString(reason));
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
