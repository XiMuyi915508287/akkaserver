package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.extension.ICommand;

public class RequestHandlerUtil {
    static {
        reload(new EmptyHanlder(CommandConst.LOGOUT));
    }

    public static void reload(IRequestHandler handler) {
        IRequestHandlerFactory factory = ContextResolver.getRequestHandlerFactory();
        factory.reload(handler);
    }

    public static IRequestHandler find(ICommand cmd){
        IRequestHandlerFactory factory = ContextResolver.getRequestHandlerFactory();
        return factory.find(cmd);
    }
}
