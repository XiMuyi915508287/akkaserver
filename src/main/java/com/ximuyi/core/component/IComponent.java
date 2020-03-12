package com.ximuyi.core.component;

public interface IComponent {

    /**
     * NONE
     * @return
     */
    String getName();

    /***
     * 初始化
     * @throws Throwable
     */
    void init() throws Throwable;

    /***
     * 同步调用而设置的接口
     * @param obj
     * @param sender
     */
    void execute(Object obj, IComponent sender);

    /**
     * 撤销
     */
    void destroy();
}
