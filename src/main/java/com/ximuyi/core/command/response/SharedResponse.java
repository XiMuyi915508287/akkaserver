package com.ximuyi.core.command.response;

import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.ICommand;
import io.netty.buffer.ByteBuf;

public class SharedResponse implements ICommandResponse {

    private ByteBuf original;
    private final ByteResponse byteResponse;
    private final Object lockObject = new Object();

    public SharedResponse(ICommand command, ResultCode resultCode, Object message) {
        this.byteResponse = new ByteResponse(command, resultCode, message);
    }

    @Override
    public ICommand getCommand() {
        return byteResponse.getCommand();
    }

    @Override
    public long getUniqueId() {
        return byteResponse.getUniqueId();
    }

    @Override
    public ByteBuf toByteBuf() {
        synchronized (lockObject){
            if (original == null){
                original = byteResponse.toByteBuf();
            }
            else {
                //original初始化为1, duplicate 不会+1, retain+1
                original.duplicate().retain();
            }
            return original;
        }
    }
}
