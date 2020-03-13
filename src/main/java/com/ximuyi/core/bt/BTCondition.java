package com.ximuyi.core.bt;

public abstract class BTCondition<T extends BTContext> extends BTNode<T> {

    @Override
    public boolean enableRun(T context) {
        return enable(context);
    }

    protected abstract boolean enable(T context);
}
