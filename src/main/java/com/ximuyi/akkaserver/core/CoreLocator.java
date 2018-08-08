package com.ximuyi.akkaserver.core;

import akka.actor.ActorSystem;
import com.ximuyi.akkaserver.api.ITaskManager;
import com.ximuyi.akkaserver.component.Component;

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
    ITaskManager getScheduler();

    /**
     * 放入自定义的服务（公用服务）.
     *
     * @param type the new manager
     */
    void setManager(Component type);

    /**
     * 获得Akka Actor系统.
     *
     * @return the actor system
     */
    ActorSystem getActorSystem();

}
