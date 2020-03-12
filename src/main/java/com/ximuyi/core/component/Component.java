package com.ximuyi.core.component;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Component implements IComponent {

    private static Logger logger = LoggerFactory.getLogger(Component.class);

    private final String name;
    private MatchConsumer matchConsumer;

    public Component(String name) {
        this.name = name;
        this.matchConsumer = buildMatchConsumer(this::onBuildMatchConsumer);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init() throws Throwable {
    }

    @Override
    public final void execute(Object obj, IComponent sender) {
        if (obj == null){
            logger.error(" null from {} error.", sender.getName());
        }
        try {
            matchConsumer.handle(obj, sender);
        }
        catch (Throwable t){
            logger.error("sycReceive [{}] from {} error.", obj.toString(), sender.getName(), t);
        }
    }

    @Override
    public void destroy() {
    }

    protected void onBuildMatchConsumer(MatchConsumer matchConsumer){

    }

    private MatchConsumer buildMatchConsumer(Consumer<MatchConsumer> consumer){
        MatchConsumer matchConsumer = new MatchConsumer();
        consumer.accept(matchConsumer);
        return matchConsumer;
    }
}
