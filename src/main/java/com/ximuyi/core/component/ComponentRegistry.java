package com.ximuyi.core.component;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;

public class ComponentRegistry implements IComponentRegistry {

    private Logger logger = LoggerFactory.getLogger(ComponentRegistry.class);

    private final LinkedHashSet<IComponent> componentSet;

    public ComponentRegistry() {
        componentSet = Sets.newLinkedHashSet();
    }

    @Override
    public Iterator<IComponent> iterator() {
        return Collections.unmodifiableSet(componentSet).iterator();
    }

    public <T> T getComponent(Class<T> type) {
        Object matchComponent = null;
        for (Object component : componentSet) {
            if (type.isAssignableFrom(component.getClass())) {
                if (matchComponent != null) {
                    throw new MissingResourceException("more than one matching component", type.getName(), null);
                }
                matchComponent = component;
            }
        }
        if (matchComponent == null) {
            throw new MissingResourceException("no matching components", type.getName(), null);
        }
        return type.cast(matchComponent);
    }

    /**
     * Adds the component.
     * @param component the component
     */
    public void addComponent(IComponent component) {
        componentSet.add(component);
        logger.info("ComponentRegistry add component " + component.getClass().getName());
    }
}
