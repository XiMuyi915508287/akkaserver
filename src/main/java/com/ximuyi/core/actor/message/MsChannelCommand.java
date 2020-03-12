package com.ximuyi.core.actor.message;


import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.session.channel.NetChannel;

public class MsChannelCommand {

    private final NetChannel netChannel;
    private final ICommand command;
    private final Object message;

    public MsChannelCommand(NetChannel netChannel, ICommand command, Object message) {
        this.netChannel = netChannel;
        this.command = command;
        this.message = message;
    }

    public NetChannel getNetChannel() {
        return netChannel;
    }

    public ICommand getCommand() {
        return command;
    }

    public Object getMessage() {
        return message;
    }
}
