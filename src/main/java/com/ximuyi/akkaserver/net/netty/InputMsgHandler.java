package com.ximuyi.akkaserver.net.netty;

import com.ximuyi.akkaserver.coder.MessageCoderUtil;
import com.ximuyi.akkaserver.extension.Command;
import com.ximuyi.akkaserver.extension.ICommand;
import com.ximuyi.akkaserver.io.IoUpMessage;
import com.ximuyi.akkaserver.session.channel.ChannelInteractor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class InputMsgHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ChannelInteractor session = AttrManager.getSession(ctx.channel());
        if (session.isClose()){
            //已经关闭了，不接受消息了
            return;
        }
        ICommand extCmd = Command.build(msg);
        Object object = MessageCoderUtil.decode(extCmd, msg);
        if (object == null){
            //包有问题，关掉它
            ChannelHandler.notifyAkkaUserClose(ctx.channel(), true,"decode error.");
        }
        else {
            session.receiveIoMessage(new IoUpMessage(extCmd, object));
        }
    }
}
