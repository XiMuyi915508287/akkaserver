package com.ximuyi.akkaserver.extension.request;

import com.ximuyi.akkaserver.extension.ICommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandlerFactory implements IRequestHandlerFactory {

    private final Map<Integer, IRequestHandler> handlers = new ConcurrentHashMap<>();

    public void reload(IRequestHandler handler){
        ICommand cmd = handler.getCmd();
        handlers.put(cmd.getUniqueId(), handler);
    }

    @Override
    public IRequestHandler find(ICommand cmd) {
        return handlers.get(cmd.getUniqueId());
    }
}
