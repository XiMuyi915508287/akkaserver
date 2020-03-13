package com.ximuyi.core;

import com.ximuyi.core.api.IAppListener;
import com.ximuyi.core.api.IScheduleManager;
import com.ximuyi.core.api.login.IUserHelper;
import com.ximuyi.core.command.handler.ICommandHandlerFactory;
import com.ximuyi.core.component.IComponent;
import com.ximuyi.core.component.IComponentRegistry;

class AkkaAppContext {

    private final String appName;

    /** The manager components. */
    protected final IComponentRegistry managerComponents;

    /** The service components. */
    protected final IComponentRegistry serviceComponents;

    /** The app listener. */
    private IAppListener appListener;

    /** schedule manager. **/
    private IScheduleManager scheduler;

    /** schedule manager. **/
    @SuppressWarnings("rawtypes")
    private IUserHelper userHelper;

    /***/
    private ICommandHandlerFactory requestHandlerFactory;

    public AkkaAppContext(String appName, IComponentRegistry managerComponents, IComponentRegistry serviceComponents) {
        this.appName = appName;
        this.managerComponents = managerComponents;
        this.serviceComponents = serviceComponents;
    }

    protected void setAppListener(IAppListener appListener) {
        this.appListener = appListener;
    }

    public IScheduleManager getScheduler() {
        return scheduler;
    }

    protected void setScheduler(IScheduleManager scheduler) {
        this.scheduler = scheduler;
    }

    @SuppressWarnings("rawtypes")
    public IUserHelper getUserHelper() {
        return userHelper;
    }

    @SuppressWarnings("rawtypes")
    protected void setUserHelper(IUserHelper userHelper) {
        this.userHelper = userHelper;
    }

    public IAppListener getAppListener() {
        return appListener;
    }

    public ICommandHandlerFactory getRequestHandlerFactory() {
        return requestHandlerFactory;
    }

    protected void setCommandHandlerFactory(ICommandHandlerFactory commandHandlerFactory) {
        this.requestHandlerFactory = commandHandlerFactory;
    }

    public <T> T getComponent(Class<T> type) {
        return serviceComponents.getComponent(type);
    }

    public <T> T getManager(Class<T> type) {
        return managerComponents.getComponent(type);
    }

    public void addManager(IComponent component){
        managerComponents.addComponent(component);
    }
}
