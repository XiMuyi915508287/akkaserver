package com.ximuyi.akkaserver.extension.response;

import com.ximuyi.akkaserver.coder.MessageCoderUtil;
import com.ximuyi.akkaserver.coder.ResponseCode;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.io.IoDownMessage;

public class IoDChannelClose extends IoDownMessage {

    public IoDChannelClose(String reason) {
        super(CommandConst.CHANNEL_CLOSE, ResponseCode.SUCCESS, MessageCoderUtil.wrap(reason));
    }
}
