package com.ximuyi.core.bt;

public class BTNode_True<T extends BTContext> extends BTNode<T> {

    @Override
    public boolean enableRun(T context) {
        return true;
    }
}
