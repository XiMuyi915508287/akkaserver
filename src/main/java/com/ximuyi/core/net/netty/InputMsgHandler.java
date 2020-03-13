package com.ximuyi.core.net.netty;

import akka.actor.ActorRef;
import com.ximuyi.core.actor.message.MsChannelCommand;
import com.ximuyi.core.command.Command;
import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.command.handler.CommandHandlerUtil;
import com.ximuyi.core.ContextResolver;
import com.ximuyi.core.session.SessionManager;
import com.ximuyi.core.session.UserSession;
import com.ximuyi.core.session.channel.ChannelCloseReason;
import com.ximuyi.core.session.channel.NetChannel;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class InputMsgHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        NetChannel netChannel = AttrManager.getSession(ctx.channel());
        if (netChannel.isClosed()){
            //已经关闭了，不接受消息了
            return;
        }
        ICommand command = Command.readCommand(msg);
        Object object = CommandHandlerUtil.decode(command, msg);
        if (object != null){
            MsChannelCommand channelCommand = new MsChannelCommand(netChannel, command, object);
            UserSession session = SessionManager.getInstance().getUserSession(netChannel.getBindUserId());
            if (session != null){
                session.tell(channelCommand, ActorRef.noSender());
            }
            else {
                ContextResolver.getSessionActor().tell(channelCommand, ActorRef.noSender());
            }
        }
        else {
            ChannelHandler.notifySessionClose(ctx.channel(), false, ChannelCloseReason.DECODE_ERROR);
        }
    }
}
