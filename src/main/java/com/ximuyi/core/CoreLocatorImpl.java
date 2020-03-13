package com.ximuyi.core;

import com.ximuyi.core.api.IScheduleManager;
import com.ximuyi.core.component.Component;

public class CoreLocatorImpl implements CoreLocator {

    @Override
    public <T> T getManager(Class<T> type) {
        return ContextResolver.getManager(type);
    }

    @Override
    public IScheduleManager getScheduler() {
        return ContextResolver.getContext().getScheduler();
    }

    @Override
    public void setManager(Component type) {
        ContextResolver.getContext().addManager(type);
    }
}
