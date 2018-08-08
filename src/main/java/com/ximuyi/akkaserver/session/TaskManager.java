package com.ximuyi.akkaserver.session;

import akka.actor.*;
import com.ximuyi.akkaserver.api.ITaskManager;
import com.ximuyi.akkaserver.message.MsRunnable;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class TaskManager implements ITaskManager {
    private final ActorRef actorRef;
    private final ActorContext context;

    public TaskManager(ActorRef actorRef,  ActorContext context) {
        this.actorRef = actorRef;
        this.context = context;
    }

    @Override
    public Cancellable scheduleOnce(Runnable runnable) {
        return schedule((dispatcher, scheduler) -> scheduler.scheduleOnce(Duration.Zero(), actorRef, new MsRunnable(runnable), dispatcher, actorRef));
    }

    @Override
    public Cancellable scheduleOnce(long delay, TimeUnit timeUnit, Runnable runnable) {
        return schedule((dispatcher, scheduler) -> {
            FiniteDuration duration = Duration.create(delay, timeUnit);
            return scheduler.scheduleOnce(duration, actorRef, new MsRunnable(runnable), dispatcher, actorRef);
        });
    }

    @Override
    public Cancellable schedule(long delay, long period, TimeUnit timeUnit, Runnable runnable) {
        return schedule((dispatcher, scheduler) -> scheduler.schedule(Duration.create(delay, timeUnit),
                                                                      Duration.create(period, timeUnit),
                                                                      actorRef, new MsRunnable(runnable), dispatcher, actorRef));
    }

    private Cancellable schedule(ISchedule scheduleCall){
        ActorSystem system = context.system();
        Scheduler scheduler = system.scheduler();
        return scheduleCall.call(context.dispatcher(), scheduler);
    }

    private interface ISchedule {
        Cancellable call(ExecutionContextExecutor dispatcher, Scheduler scheduler);
    }

}
