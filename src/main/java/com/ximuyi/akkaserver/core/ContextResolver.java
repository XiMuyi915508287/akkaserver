package com.ximuyi.akkaserver.core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import com.ximuyi.akkaserver.api.AppListener;
import com.ximuyi.akkaserver.api.login.IUserHelper;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.extension.request.IRequestHandlerFactory;

public class ContextResolver {

    private static AkkaAppContext context;

    private ContextResolver()
    {}

    public static ConfigWrapper getConfig()
    {
        return context.getConfig();
    }


    public static AppListener getAppListener()
    {
        return context.getAppListener();
    }

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

    public static IRequestHandlerFactory getRequestHandlerFactory()
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


    public static Inbox getSystemInbox()
    {
        AkkaMediator mediator = getComponent(AkkaMediator.class);
        return mediator.getSystemInbox();
    }

    public static ActorRef getSessionActor()
    {
        AkkaMediator mediator = getComponent(AkkaMediator.class);
        return mediator.getSessionActor();
    }

    public static ActorRef getUserActor()
    {
        AkkaMediator service = getComponent(AkkaMediator.class);
        return service.getUserActor();
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
