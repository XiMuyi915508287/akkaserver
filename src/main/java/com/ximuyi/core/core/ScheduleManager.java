package com.ximuyi.core.core;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import com.ximuyi.core.api.IScheduleManager;
import scala.concurrent.duration.Duration;

public class ScheduleManager implements IScheduleManager {

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
        return scheduler.scheduleOnce(Duration.create(delay,timeUnit), runnable, system.dispatcher());
    }

    @Override
    public Cancellable schedule(long delay, long period, TimeUnit timeUnit, Runnable runnable) {
        ActorSystem system = ContextResolver.getActorSystem();
        Scheduler scheduler = system.scheduler();
        return scheduler.schedule(Duration.create(delay, timeUnit), Duration.create(period, timeUnit), runnable, system.dispatcher());
    }
}
