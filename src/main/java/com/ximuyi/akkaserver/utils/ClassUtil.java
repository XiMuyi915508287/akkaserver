package com.ximuyi.akkaserver.utils;

public class ClassUtil {

    public static <T> T newInstance(String clsName) {
        T object = null;
        try {
            object = (T)Class.forName(clsName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("构造失败:%s ",clsName, e));
        }
        return object;
    }

    public static <T> T newInstance(Class<?> cls) {
        T object = null;
        try {
            object = (T) cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("构造失败:%s ",cls.getName(), e));
        }
        return object;
    }
}
