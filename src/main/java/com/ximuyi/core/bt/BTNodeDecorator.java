package com.ximuyi.core.bt;

public class BTNodeDecorator<T extends BTContext> extends BTNode<T> {

    private BTNode<T> decorator;

    @Override
    public boolean enableRun(T context) {
        return decorator.run(context);
    }

    protected void setDecorator(BTNode<T> decorator) {
        this.decorator = decorator;
    }
}
