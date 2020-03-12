package com.ximuyi.core.command.handler.ex;

import java.util.List;

import com.ximuyi.core.coder.MessageCoderUtil;
import com.ximuyi.core.command.CommandUtil;
import com.ximuyi.core.command.handler.CommandHandler;
import com.ximuyi.core.command.response.ByteResponse;
import com.ximuyi.core.session.UserSession;
import com.ximuyi.core.session.channel.NetChannel;
import com.ximuyi.core.user.IUserSession;
import com.ximuyi.core.user.InnerUser;
import io.netty.buffer.ByteBuf;

public class CacheResponseHandler extends CommandHandler<Long> implements ISystemHandler{

    public CacheResponseHandler() {
        super(CommandUtil.CACHE_RESPONSE);
    }

    @Override
    protected void handleCommandRequest(IUserSession session, Long message) {
        InnerUser innerUser = ((UserSession) session).getInnerUser();
        NetChannel netChannel = ((UserSession) session).getNetChannel();
        List<ByteResponse> responseList = innerUser.getCacheResponse(message);
        for (ByteResponse response : responseList) {
            netChannel.addCommandResponse(response);
        }
    }

    @Override
    public Object decode(ByteBuf byteBuf) {
        return MessageCoderUtil.byteLong(byteBuf);
    }
}
