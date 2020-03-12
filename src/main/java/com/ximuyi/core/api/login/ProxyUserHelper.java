package com.ximuyi.core.api.login;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyUserHelper implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyUserHelper.class);

    @SuppressWarnings("rawtypes")
    private final IUserHelper helper;

    @SuppressWarnings("rawtypes")
    public ProxyUserHelper(IUserHelper helper) {
        this.helper = helper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            Object object = method.invoke(helper, args);
            log(method, args, "success", null);
            return object;
        }
        catch (Throwable t) {
            log(method, args, "failure", t);
            return null;    // 目前无返回值的函数，都是通知形式，直接捕获异常就可以
        }
    }

    private void log(Method method, Object[] args, String message, Throwable t){
        if (t != null || logger.isInfoEnabled()){
            StringBuilder builder = new StringBuilder();
            for (Object arg : args) {
                if (arg.getClass().isPrimitive()){
                    builder.append(" ").append(arg);
                }
                else {
                    String string = ToStringBuilder.reflectionToString(arg, ToStringStyle.JSON_STYLE);
                    builder.append(" ").append(string);
                }
            }
            if (t == null){
                logger.info("method:{} executed {}, {}", method.getName(), message, builder);
            }
            else {
                logger.error("method:{} executed {}, {}", method.getName(), message, builder, t);
            }
        }
    }
}
