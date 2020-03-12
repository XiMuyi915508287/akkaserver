package com.ximuyi.core.command.handler.ex;

import com.ximuyi.core.coder.MessageCoderUtil;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.CommandUtil;
import com.ximuyi.core.command.handler.CommandHandler;
import com.ximuyi.core.user.IUserSession;
import io.netty.buffer.ByteBuf;

public class HeartBeatHandler extends CommandHandler<Object> implements ISystemHandler {

    public HeartBeatHandler() {
        super(CommandUtil.HEART_BEAT);
    }

    @Override
    protected void handleCommandRequest(IUserSession session, Object message) {
        Object o = MessageCoderUtil.encodeLong(System.currentTimeMillis());
        session.cacheAndResponse(getCommand(), ResultCode.SUCCESS, o);
    }

    @Override
    public Object decode(ByteBuf byteBuf) {
        return MessageCoderUtil.empty();
    }
}
