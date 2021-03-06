package com.ximuyi.core.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.ximuyi.core.exception.ThreadUncaughtExceptionHandler;

public class PoolThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private final String namePrefix;
    private final Boolean daemon;

    public PoolThreadFactory(String namePrefix) {
        // daemon随创建线程
        this(namePrefix, null);
    }

    public PoolThreadFactory(String namePrefix, Boolean daemon) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + "-" + threadNumber.getAndIncrement(), 0);
        t.setUncaughtExceptionHandler(ThreadUncaughtExceptionHandler.getHandler());
        // The newly created thread is initially marked as being a daemon thread if and only if
        // the thread creating it is currently marked as a daemon thread.
        if (this.daemon != null) {
            t.setDaemon(this.daemon);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }
}
