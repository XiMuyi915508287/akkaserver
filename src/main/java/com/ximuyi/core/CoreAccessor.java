package com.ximuyi.core;

import com.ximuyi.core.api.IScheduleManager;
import com.ximuyi.core.component.Component;

public class CoreAccessor implements CoreLocator {

    private static final CoreAccessor instance = new CoreAccessor();

    public static CoreAccessor getInstance() {
        return instance;
    }

    private CoreAccessor() {
    }
    private CoreLocator locator;

    protected void setLocator(CoreLocator myLocator) {
        if (locator != null){
            throw new UnsupportedOperationException();
        }
        locator = myLocator;
    }

    @Override
    public <T> T getManager(Class<T> type) {
        return locator.getManager(type);
    }

    @Override
    public IScheduleManager getScheduler() {
        return locator.getScheduler();
    }

    @Override
    public void setManager(Component type) {
        locator.setManager(type);
    }
}
