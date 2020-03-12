package com.ximuyi.core.command.handler;

import com.ximuyi.core.command.ICommand;

public interface ICommandHandlerFactory {

    void reload(ICommandHandler handler);

    ICommandHandler find(ICommand cmd);
}
