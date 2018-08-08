package com.ximuyi.akkaserver.coder;

import com.ximuyi.akkaserver.core.ContextResolver;
import com.ximuyi.akkaserver.extension.ICommand;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCoderUtil {

    private static final Logger logger = LoggerFactory.getLogger(MessageCoderUtil.class);

    private static MessageCoder coder = ContextResolver.getManager(MessageCoder.class);

    public static byte[] encode(ICommand extCmd, Object obj){
        try {
            return coder.encode(extCmd, obj);
        }
        catch (Throwable t){
            logger.error("{} encode error.", extCmd.toString());
            return null;
        }
    }

    public static Object decode(ICommand extCmd, ByteBuf buffer){
        try {
            return coder.decode(extCmd, buffer);
        }
        catch (Throwable t){
            logger.error("{} decode error.", extCmd.toString(), t);
            return null;
        }
    }

    public static Object wrap(String string){
        return coder.wrap(string);
    }
}
