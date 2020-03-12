package com.ximuyi.core.command;

import java.util.Objects;

import io.netty.buffer.ByteBuf;

public class Command implements ICommand {

    private final short extension;
    private final short cmd;
    private final int uniqueId;

    public Command(short extension, short cmd) {
        this.extension = extension;
        this.cmd = cmd;
        int unique = extension;
        this.uniqueId = (unique << 16) | cmd;
    }

    public int getUniqueId(){
        return uniqueId;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeShort(extension);
        byteBuf.writeShort(cmd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return uniqueId == command.uniqueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    @Override
    public String toString() {
        return "{" +
                "extension=" + extension +
                ", cmd=" + cmd +
                ", uniqueId=" + uniqueId +
                '}';
    }

    public static ICommand readCommand(ByteBuf buf){
        short extension = buf.readShort();
        short cmd = buf.readShort();
        return new Command(extension, cmd);
    }
}
