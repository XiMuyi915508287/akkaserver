package com.ximuyi.core.command.response;

import com.ximuyi.core.command.ICommand;

public abstract class CommandResponse implements ICommandResponse {

    private final ICommand command;
    private final long uniqueId;

    public CommandResponse(ICommand command) {
       this(command, 0);
    }

    public CommandResponse(ICommand command, long uniqueId) {
        this.command = command;
        this.uniqueId = uniqueId;
    }

    public ICommand getCommand() {
        return command;
    }

    public long getUniqueId() {
        return uniqueId;
    }
}
