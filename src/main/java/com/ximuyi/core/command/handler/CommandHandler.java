package com.ximuyi.core.command.handler;

import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.user.IUserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommandHandler<T0> implements ICommandHandler<T0> {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private final ICommand command;

    public CommandHandler(ICommand command) {
        this.command = command;
    }

    @Override
    public void execute(IUserSession session, T0 message) {
        try {
            handleCommandRequest(session, message);
            logger.debug("userId:{} account:{} handle command:{} success, message:{}",session.getUserId(), session.getAccount(), command, message);
        }
        catch (Throwable t){
            logger.error("userId:{} account:{} handle command:{} error, message:{}",session.getUserId(), session.getAccount(), command, message, t);
        }
        finally {
        }
    }

    @Override
    public final ICommand getCommand() {
        return command;
    }

    protected abstract void handleCommandRequest(IUserSession session, T0 message) throws Throwable;
}
