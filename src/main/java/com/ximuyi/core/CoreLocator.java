package com.ximuyi.core;

import com.ximuyi.core.api.IScheduleManager;
import com.ximuyi.core.component.Component;

public interface CoreLocator {
    /**
     * Gets the manager.
     *
     * @param <T> the generic type
     * @param type the type
     * @return the manager
     */
    <T> T getManager(Class<T> type);

    /**
     * 获得全局任务管理器.
     *
     * @return the globle task manager
     */
    IScheduleManager getScheduler();

    /**
     * 放入自定义的服务（公用服务）.
     *
     * @param type the new manager
     */
    void setManager(Component type);
}
