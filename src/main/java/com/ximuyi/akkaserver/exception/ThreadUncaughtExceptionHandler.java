package com.ximuyi.akkaserver.exception;

import java.util.concurrent.TimeUnit;

public class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static Thread.UncaughtExceptionHandler instance = new ThreadUncaughtExceptionHandler();

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(t.getName() + " " + e);
    }

    public static Thread.UncaughtExceptionHandler getHandler() {
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new ThreadException("execption");
        //设置捕捉异常，为了可以做日志重定向，资源回收等， 不设置的话使用 GroupThread.uncaughtException
        //Thread.dispatchUncaughtException 获取 UncaughtExceptionHandler
        thread.setUncaughtExceptionHandler(ThreadUncaughtExceptionHandler.getHandler());
        //使用@Test测试，设置daemon为false，线程也会推出的，注意这一点
        Thread thread1 = new ThreadDemo("daemon", false);
        thread1.start();
        TimeUnit.SECONDS.sleep(2);
        thread.start();
        TimeUnit.SECONDS.sleep(3);
        //只要线程异常了，就会垮掉，但是不影响整个进程的运行
        System.out.println(  thread.isAlive());
    }

    private static class ThreadDemo extends Thread{
        public ThreadDemo(String name, boolean isDaemon) {
            super(name);
            setDaemon(isDaemon);
        }

        @Override
        public void run() {
            while (true){
                System.out.println(getName() + "...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static class ThreadException extends Thread{
        public ThreadException(String name) {
            super(name);
        }

        @Override
        public void run() {
            throw new UnsupportedOperationException();
        }
    }
}
