package com.ximuyi.core.command.response;

import com.ximuyi.core.coder.MessageCoderUtil;
import com.ximuyi.core.coder.ResultCode;
import com.ximuyi.core.command.ICommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ByteResponse extends CommandResponse {

    private static final int INT_LENGTH = 4;

    private final Object message;
    private final short resultCode;

    public ByteResponse(ICommand command, ResultCode resultCode, Object message) {
        super(command);
        this.message = message;
        this.resultCode = resultCode.code();
    }

    public ByteResponse(ICommand command, long uniqueId, ResultCode resultCode, Object message) {
        super(command, uniqueId);
        this.message = message;
        this.resultCode = resultCode.code();
    }

    public Object getMessage() {
        return message;
    }

    @Override
    public ByteBuf toByteBuf() {
        //|包长|唯一标示|结果码|内容
        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer();
        byteBuf.writeInt(0);
        byteBuf.writeLong(getUniqueId());
        getCommand().write(byteBuf);
        byteBuf.writeShort(resultCode);
        if (message != null){
            byte[] bytes = MessageCoderUtil.encode(getCommand(), message);
            if (bytes != null){
                byteBuf.writeBytes(bytes);
            }
            else {
                byteBuf.setShort(INT_LENGTH, ResultCode.ENCODE_EXCEPTION.code());
            }
        }
        int length = byteBuf.readableBytes();
        byteBuf.setInt(0,  length - INT_LENGTH);
        return byteBuf;
    }

    @Override
    public String toString() {
        return "{" +
                "command=" + ToStringBuilder.reflectionToString(getCommand(), ToStringStyle.JSON_STYLE) +
                "uniqueId=" + getUniqueId() +
                "message=" + ToStringBuilder.reflectionToString(message, ToStringStyle.JSON_STYLE) +
                ", resultCode=" + resultCode +
                '}';
    }
}
