package com.ximuyi.akkaserver.component;

import com.ximuyi.akkaserver.config.ConfigWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public abstract class Component implements IComponent {

    private static Logger logger = LoggerFactory.getLogger(Component.class);

    private final String name;
    private ReceiveAction receive;

    public Component(String name) {
        this.name = name;
        this.receive = build(this::buildSycReceive);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(ConfigWrapper config) throws Throwable {
    }

    @Override
    public final void sycReceive(Object obj, IComponent sender) {
        if (obj == null){
            logger.error("sycReceive null from {} error.", sender.getName());
        }
        try {
            receive.handle(obj, sender);
        }
        catch (Throwable t){
            logger.error("sycReceive [{}] from {} error.", obj.toString(), sender.getName(), t);
        }
    }

    @Override
    public void destroy() {
    }

    protected abstract void buildSycReceive(ReceiveAction receive);

    private ReceiveAction build(Consumer<ReceiveAction> consumer){
        ReceiveAction componentReceive = new ReceiveAction();
        consumer.accept(componentReceive);
        return componentReceive;
    }
}
