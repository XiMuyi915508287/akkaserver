package com.ximuyi.core.bt;

public class BTNode_None<T extends BTContext> extends BTNode<T> {

    @Override
    public boolean enableRun(T context) {
        return false;
    }
}
