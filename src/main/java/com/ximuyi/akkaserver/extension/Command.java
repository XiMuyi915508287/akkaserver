package com.ximuyi.akkaserver.extension;

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
    public void wirte(ByteBuf byteBuf) {
        byteBuf.writeShort(extension);
        byteBuf.writeShort(cmd);
    }


    @Override
    public String toString() {
        return "{" +
                "extension=" + extension +
                ", cmd=" + cmd +
                '}';
    }

    public static ICommand build(ByteBuf buf){
        short extension = buf.readShort();
        short cmd = buf.readShort();
        return new Command(extension, cmd);
    }
}
