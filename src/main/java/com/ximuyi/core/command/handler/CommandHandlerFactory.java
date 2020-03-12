package com.ximuyi.core.command.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ximuyi.core.command.ICommand;

public class CommandHandlerFactory implements ICommandHandlerFactory {

    private final Map<Integer, ICommandHandler> handlers = new ConcurrentHashMap<>();

    public void reload(ICommandHandler handler){
        ICommand cmd = handler.getCommand();
        handlers.put(cmd.getUniqueId(), handler);
    }

    @Override
    public ICommandHandler find(ICommand command) {
        return handlers.get(command.getUniqueId());
    }
}
