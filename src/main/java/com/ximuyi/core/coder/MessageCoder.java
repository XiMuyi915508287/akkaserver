package com.ximuyi.core.coder;

import com.ximuyi.core.component.Component;

//        if (CommandUtil.equals(command, CommandConst.LOGOUT)){
//                return empty();
//                }
//                else if (CommandUtil.equals(command, CommandConst.HEART_BEAT)){
//                return empty();
//                }
//                else if (CommandUtil.equals(command, CommandConst.GET_RESPONSE)){
//                return empty();
//                }
public abstract class MessageCoder<T> extends Component implements IMessageCoder<T>{

    public MessageCoder(String name) {
        super(name);
    }
}
