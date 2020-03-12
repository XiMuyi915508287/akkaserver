package com.ximuyi.core.command.handler;

import com.ximuyi.core.coder.MessageCoderUtil;
import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.command.handler.ex.CacheResponseHandler;
import com.ximuyi.core.command.handler.ex.HeartBeatHandler;
import com.ximuyi.core.command.handler.ex.ISystemHandler;
import com.ximuyi.core.command.handler.ex.LogoutHandler;
import com.ximuyi.core.core.ContextResolver;
import io.netty.buffer.ByteBuf;

public class CommandHandlerUtil {

    static {
        reload(new LogoutHandler());
        reload(new HeartBeatHandler());
        reload(new CacheResponseHandler());
    }

    public static void reload(@SuppressWarnings("rawtypes") ICommandHandler handler) {
        ICommandHandlerFactory factory = ContextResolver.getCommandHandlerFactory();
        factory.reload(handler);
    }

    @SuppressWarnings("rawtypes")
    public static ICommandHandler lookUp(ICommand command){
        ICommandHandlerFactory factory = ContextResolver.getCommandHandlerFactory();
        return factory.find(command);
    }

    public static Object decode(ICommand command, ByteBuf byteBuf){
        ICommandHandler handler = lookUp(command);
        if (handler instanceof ISystemHandler){
            return ((ISystemHandler)handler).decode(byteBuf);
        }
        else {
            return MessageCoderUtil.decode(command, byteBuf);
        }
    }
}
