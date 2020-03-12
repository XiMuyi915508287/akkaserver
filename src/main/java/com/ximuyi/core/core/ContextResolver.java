package com.ximuyi.core.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.ximuyi.core.api.AppListener;
import com.ximuyi.core.api.login.IUserHelper;
import com.ximuyi.core.command.handler.ICommandHandlerFactory;

public class ContextResolver {

    private static AkkaAppContext context;

    private ContextResolver()
    {}

    public static AppListener getAppListener()
    {
        return context.getAppListener();
    }

    @SuppressWarnings("rawtypes")
    public static IUserHelper getUserHelper()
    {
        return context.getUserHelper();
    }

    public static ScheduleManager getScheduler() {
        return context.getScheduler();
    }

    public static <T> T getManager(Class<T> type)
    {
        return context.getManager(type);
    }

    public static ICommandHandlerFactory getCommandHandlerFactory()
    {
        return context.getRequestHandlerFactory();
    }

    public static <T> T getComponent(Class<T> type)
    {
        return context.getComponent(type);
    }

    public static ActorSystem getActorSystem()
    {
        AkkaMediator service = getComponent(AkkaMediator.class);
        return service.getSystem();
    }

    public static ActorRef getSessionActor()
    {
        AkkaMediator mediator = getComponent(AkkaMediator.class);
        return mediator.getSessionRef();
    }

    static AkkaAppContext getContext()
    {
        return context;
    }

    static void setContext(AkkaAppContext ctx)
    {
        context = ctx;
    }

}
