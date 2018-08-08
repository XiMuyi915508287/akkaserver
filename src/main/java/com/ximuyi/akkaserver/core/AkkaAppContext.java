package com.ximuyi.akkaserver.core;

import com.ximuyi.akkaserver.api.AppListener;
import com.ximuyi.akkaserver.api.login.IUserHelper;
import com.ximuyi.akkaserver.component.ComponentRegistry;
import com.ximuyi.akkaserver.component.IComponent;
import com.ximuyi.akkaserver.component.IComponentRegistry;
import com.ximuyi.akkaserver.config.ConfigWrapper;
import com.ximuyi.akkaserver.extension.request.IRequestHandlerFactory;

class AkkaAppContext {

    private final String appName;

    private final ConfigWrapper config;

    /** The manager components. */
    protected final IComponentRegistry managerComponents;

    /** The service components. */
    protected final IComponentRegistry serviceComponents;

    /** The app listener. */
    private AppListener appListener;

    /** schedule manager. **/
    private ScheduleManager scheduler;

    /** schedule manager. **/
    private IUserHelper userHelper;

    /***/
    private IRequestHandlerFactory requestHandlerFactory;

    public AkkaAppContext(String appName, ConfigWrapper config, IComponentRegistry managerComponents, IComponentRegistry serviceComponents) {
        this.appName = appName;
        this.config = config;
        this.managerComponents = managerComponents;
        this.serviceComponents = serviceComponents;
    }

    protected void setAppListener(AppListener appListener) {
        this.appListener = appListener;
    }

    public ScheduleManager getScheduler() {
        return scheduler;
    }

    protected void setScheduler(ScheduleManager scheduler) {
        this.scheduler = scheduler;
    }

    public IUserHelper getUserHelper() {
        return userHelper;
    }

    protected void setUserHelper(IUserHelper userHelper) {
        this.userHelper = userHelper;
    }

    public ConfigWrapper getConfig() {
        return config;
    }

    public AppListener getAppListener() {
        return appListener;
    }

    public IRequestHandlerFactory getRequestHandlerFactory() {
        return requestHandlerFactory;
    }

    protected void setRequestHandlerFactory(IRequestHandlerFactory requestHandlerFactory) {
        this.requestHandlerFactory = requestHandlerFactory;
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
