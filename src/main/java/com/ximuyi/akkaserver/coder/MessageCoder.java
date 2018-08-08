package com.ximuyi.akkaserver.coder;

import com.ximuyi.akkaserver.component.Component;
import com.ximuyi.akkaserver.component.ReceiveAction;
import com.ximuyi.akkaserver.extension.CommandConst;
import com.ximuyi.akkaserver.extension.CommandUtil;
import com.ximuyi.akkaserver.extension.ICommand;
import io.netty.buffer.ByteBuf;

public abstract class MessageCoder<T> extends Component {

    public MessageCoder(String name) {
        super(name);
    }

    public final byte[] encode(ICommand extCmd, T obj){
        return _encode(extCmd, obj);
    }

    public final T decode(ICommand extCmd, ByteBuf buffer) throws Throwable{
        if (CommandUtil.equals(extCmd, CommandConst.LOGOUT)){
            return buffer.readableBytes() == 0 ? empty() : null;
        }
        return _decode(extCmd, buffer);
    };

    public abstract byte[] _encode(ICommand extCmd, T obj);

    public abstract T _decode(ICommand extCmd, ByteBuf buffer) throws Throwable;

    public abstract T empty();

    public abstract T wrap(String string);

    public abstract T wrap(int value);

    @Override
    protected void buildSycReceive(ReceiveAction receive) {
    }
}
