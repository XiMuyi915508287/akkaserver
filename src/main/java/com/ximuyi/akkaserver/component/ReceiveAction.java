package com.ximuyi.akkaserver.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

public class ReceiveAction {

    private static Logger logger = LoggerFactory.getLogger(ReceiveAction.class);

    private static final BiConsumer<Object, IComponent> Default = (msg, sender) -> logger.error("unhandle msg [{}] from {}", msg.toString(), sender.getName());
    private static final Comparator<CaseConsumer> Comparator =(c0, c1) -> c0.cls.isAssignableFrom(c1.cls) ? 1 : 0;

    private CaseConsumer[] consumerUnits;
    private BiConsumer<Object, IComponent> consumerAny = Default;

    public ReceiveAction() {
        this.consumerUnits = new CaseConsumer[0];
    }

    /**
     * @param cls A extend B, A.class 会覆盖 B.calss
     * @param consumer
     * @param <T>
     * @return
     */
    public <T> ReceiveAction match(Class<T> cls, BiConsumer<T, IComponent> consumer){
        add(cls, consumer);
        return this;
    }

    public ReceiveAction matchAny(BiConsumer<Object, IComponent> consumer){
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

    private  void add(Class<?> cls, BiConsumer consumer){
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
