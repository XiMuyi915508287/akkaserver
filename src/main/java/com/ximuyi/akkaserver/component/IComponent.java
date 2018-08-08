package com.ximuyi.akkaserver.component;

import com.ximuyi.akkaserver.config.ConfigWrapper;

public interface IComponent {

    /**
     * NONE
     * @return
     */
    String getName();

    /***
     * 初始化
     * @param config
     * @throws Throwable
     */
    void init(ConfigWrapper config) throws Throwable;

    /***
     * 同步调用而设置的接口
     * @param obj
     * @param sender
     */
    void sycReceive(Object obj, IComponent sender);

    /**
     * 撤销
     */
    void destroy();
}
