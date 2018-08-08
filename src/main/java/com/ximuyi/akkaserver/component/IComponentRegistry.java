package com.ximuyi.akkaserver.component;

public interface IComponentRegistry extends Iterable<IComponent> {

    public <T> T getComponent(Class<T> type);

    public void addComponent(IComponent component);
}
