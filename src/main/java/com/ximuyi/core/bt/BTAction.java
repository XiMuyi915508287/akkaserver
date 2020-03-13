package com.ximuyi.core.bt;

public abstract class BTAction<T extends BTContext> extends BTNode<T> {

    @Override
    public boolean enableRun(T context) {
        return action(context);
    }


    protected abstract boolean action(T context);
}
