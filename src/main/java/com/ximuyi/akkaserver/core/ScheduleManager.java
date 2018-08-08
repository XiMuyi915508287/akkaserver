package com.ximuyi.akkaserver.core;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import com.ximuyi.akkaserver.api.ITaskManager;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class ScheduleManager implements ITaskManager {

    @Override
    public Cancellable scheduleOnce(Runnable runnable) {
        ActorSystem system = ContextResolver.getActorSystem();
        Scheduler scheduler = system.scheduler();
        return scheduler.scheduleOnce(Duration.Zero(), runnable, system.dispatcher());
    }

    @Override
    public Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable runnable) {
        ActorSystem system = ContextResolver.getActorSystem();
        Scheduler scheduler = system.scheduler();
        return scheduler.scheduleOnce(
                Duration.create(delay,timeUnit), runnable, system.dispatcher());
    }

    @Override
    public Cancellable schedule(long delay, long period, TimeUnit timeUnit, Runnable runnable) {
        ActorSystem system = ContextResolver.getActorSystem();
        Scheduler scheduler = system.scheduler();
        return scheduler.schedule(Duration.create(delay, timeUnit),
                                  Duration.create(period, timeUnit), runnable, system.dispatcher());
    }
}
