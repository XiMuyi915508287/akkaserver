package com.ximuyi.core.api;

import java.util.concurrent.TimeUnit;

import akka.actor.Cancellable;

public interface IScheduleManager {

    /**
     * 执行任务.
     * @param runnable            可执行任务
     * @return the cancellable
     */
    Cancellable scheduleOnce(Runnable runnable);

    /**
     * 执行任务.
     * @param delay            延时多少毫秒()
     * @param runnable            可执行任务
     * @return the cancellable
     */
    Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable runnable);

    /**
     * 执行周期任务.
     * @param delay            延时多少毫秒
     * @param period            多少毫秒为一个周期
     * @param runnable            可执行任务
     * @return the cancellable
     */
    Cancellable schedule(long delay, long period, TimeUnit timeUnit, Runnable runnable);
}
