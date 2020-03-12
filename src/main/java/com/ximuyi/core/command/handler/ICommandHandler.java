package com.ximuyi.core.command.handler;

import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.user.IUserSession;

public interface ICommandHandler<T0> {

    void execute(IUserSession session, T0 message);

    ICommand getCommand();
}
