package com.ximuyi.akkaserver.core;

import akka.actor.ActorSystem;
import com.ximuyi.akkaserver.api.ITaskManager;
import com.ximuyi.akkaserver.component.Component;

public class CoreLocatorImpl implements CoreLocator {

    @Override
    public <T> T getManager(Class<T> type) {
        return ContextResolver.getManager(type);
    }

    @Override
    public ITaskManager getScheduler() {
        return ContextResolver.getContext().getScheduler();
    }

    @Override
    public void setManager(Component type) {
        ContextResolver.getContext().addManager(type);
    }

    @Override
    public ActorSystem getActorSystem() {
        return ContextResolver.getActorSystem();
    }
}
