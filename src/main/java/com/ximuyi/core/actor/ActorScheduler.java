package com.ximuyi.core.actor;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import com.ximuyi.core.api.IScheduleManager;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * 需要包装一个Runnable
 */
public class ActorScheduler implements IScheduleManager {
    private final ActorRef actorRef;
    private final ActorContext context;

    public ActorScheduler(ActorRef actorRef, ActorContext context) {
        this.actorRef = actorRef;
        this.context = context;
    }

    @Override
    public Cancellable scheduleOnce(Runnable runnable) {
        return schedule((dispatcher, scheduler) -> scheduler.scheduleOnce(Duration.Zero(), actorRef, create(runnable), dispatcher, actorRef));
    }

    @Override
    public Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable runnable) {
        return schedule((dispatcher, scheduler) -> {
            FiniteDuration duration = Duration.create(delay, timeUnit);
            return scheduler.scheduleOnce(duration, actorRef, create(runnable), dispatcher, actorRef);
        });
    }

    @Override
    public Cancellable schedule(long delay, long period, TimeUnit timeUnit, Runnable runnable) {
        return schedule((dispatcher, scheduler) -> scheduler.schedule(Duration.create(delay, timeUnit),
                                                                      Duration.create(period, timeUnit),
                                                                      actorRef, create(runnable), dispatcher, actorRef));
    }

    private Cancellable schedule(ISchedule scheduleCall){
        ActorSystem system = context.system();
        Scheduler scheduler = system.scheduler();
        return scheduleCall.call(context.dispatcher(), scheduler);
    }

    private ActorRunnable create(Runnable runnable){
        String format = String.format("ActorScheduler:%s", actorRef.path());
        return new ActorRunnable(actorRef, format, runnable);
    }

    private interface ISchedule {
        Cancellable call(ExecutionContextExecutor dispatcher, Scheduler scheduler);
    }

}
