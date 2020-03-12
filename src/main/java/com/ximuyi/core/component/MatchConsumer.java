package com.ximuyi.core.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchConsumer {

    private static Logger logger = LoggerFactory.getLogger(MatchConsumer.class);

    private static final BiConsumer<Object, IComponent> Default = (msg, sender) -> logger.error("don't handle msg [{}] from {}", msg.toString(), sender.getName());
    private static final Comparator<CaseConsumer> Comparator =(c0, c1) -> c0.cls.isAssignableFrom(c1.cls) ? 1 : 0;

    private CaseConsumer[] consumerUnits;
    private BiConsumer<Object, IComponent> consumerAny = Default;

    public MatchConsumer() {
        this.consumerUnits = new CaseConsumer[0];
    }

    /**
     * @param cls A extend B, A.class 会覆盖 B.class
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> MatchConsumer match(Class<T> cls, BiConsumer<T, IComponent> consumer){
        add(cls, consumer);
        return this;
    }

    public MatchConsumer matchAny(BiConsumer<Object, IComponent> consumer){
        this.consumerAny = consumer;
        return this;
    }

    public void handle(Object msg, IComponent sender){
        BiConsumer consumer = get(msg.getClass());
        consumer.accept(msg, sender);
    }

    private BiConsumer get(Class<?> cls){
        for (CaseConsumer consumerUnit : consumerUnits) {
            if (consumerUnit.cls.isAssignableFrom(cls)) {
                return consumerUnit.consumer;
            }
        }
        return consumerAny;
    }

    private void add(Class<?> cls, BiConsumer consumer){
        List<CaseConsumer> arrayList = new ArrayList<>();
        for (int i = 0; i < consumerUnits.length; i++) {
            if(consumerUnits[i].cls.equals(cls)){
                throw new UnsupportedOperationException(cls.getName());
            }
            arrayList.add(consumerUnits[i]);
        }
        arrayList.add(new CaseConsumer(cls, consumer));
        arrayList.sort(Comparator);
        consumerUnits = arrayList.toArray(new CaseConsumer[0]);
    }

    private class CaseConsumer {
        public final Class<?> cls;
        public final BiConsumer consumer;

        public CaseConsumer(Class<?> cls, BiConsumer consumer) {
            this.cls = cls;
            this.consumer = consumer;
        }
    }
}
