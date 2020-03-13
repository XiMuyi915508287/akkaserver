package com.ximuyi.core.command.handler.ex;

import com.ximuyi.core.actor.message.MsUserLogout;
import com.ximuyi.core.coder.MessageCoderUtil;
import com.ximuyi.core.command.CommandUtil;
import com.ximuyi.core.command.handler.CommandHandler;
import com.ximuyi.core.ContextResolver;
import com.ximuyi.core.session.UserSession;
import com.ximuyi.core.session.channel.ChannelCloseReason;
import com.ximuyi.core.session.channel.ChannelUtil;
import com.ximuyi.core.session.channel.NetChannel;
import com.ximuyi.core.user.IUserSession;
import io.netty.buffer.ByteBuf;

public class LogoutHandler extends CommandHandler<Object> implements ISystemHandler {

    public LogoutHandler() {
        super(CommandUtil.LOGOUT);
    }

    @Override
    protected void handleCommandRequest(IUserSession session, Object message) {
        ContextResolver.getUserHelper().onUserLogout(session.getUser());
        NetChannel netChannel = ((UserSession) session).getNetChannel();
        ChannelUtil.channelClose(netChannel, ChannelCloseReason.LOGOUT, false);
        ContextResolver.getSessionActor().tell(new MsUserLogout(session.getUserId()), session.getSelf());
    }

    @Override
    public Object decode(ByteBuf byteBuf) {
        return MessageCoderUtil.empty();
    }
}
