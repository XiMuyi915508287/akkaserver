package com.ximuyi.core.coder;

import com.ximuyi.core.command.ICommand;
import com.ximuyi.core.ContextResolver;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCoderUtil{

    private static final Logger logger = LoggerFactory.getLogger(MessageCoderUtil.class);

    private static MessageCoder CODER = ContextResolver.getManager(MessageCoder.class);

    public static byte[] encode(ICommand command, Object obj){
        //noinspection unchecked
        return execute("encode", command, ()-> CODER.encode(command, obj));
    }

    public static Object decode(ICommand command, ByteBuf buffer){
        return execute("decode", command, ()-> CODER.decode(command, buffer));
    }

    public static Object empty(){
        return CODER.empty();
    }

    public static Object encodeString(String string) {
        return execute("encodeString", null, ()-> CODER.encodeString(string));
    }

    public static Object encodeInt(int value) {
        return execute("encodeInt", null, ()-> CODER.encodeInt(value));
    }

    public static Object encodeLong(long value) {
        return execute("encodeLong", null, ()-> CODER.encodeLong(value));
    }

    public static String byteString(ByteBuf buffer) {
        return execute("byteString", null, ()-> CODER.byteString(buffer));
    }

    public static int byteInt(ByteBuf buffer) {
        //noinspection ConstantConditions
        return execute("byteInt", null, ()-> CODER.byteInt(buffer));
    }

    @SuppressWarnings("ConstantConditions")
    public static long byteLong(ByteBuf buffer) {
        return execute("byteLong", null, ()-> CODER.byteLong(buffer));
    }

    private static <T> T execute(String message, ICommand command, MySupplier<T> supplier){
        try {
            return supplier.get();
        }
        catch (Throwable t){
            if (command == null){
                logger.error("{} error.", message, t);
            }
            else {
                logger.error("{} command:{} error.", message, command, t);
            }
            return null;
        }
    }

    /**
     * 暂时使用挫的方法
     * @param <T>
     */
    private interface MySupplier<T> {

        default T get() {
            try {
                return get0();
            }
            catch (Throwable t){
                throw new RuntimeException(t);
            }
        }

        T get0() throws Throwable;
    }
}


