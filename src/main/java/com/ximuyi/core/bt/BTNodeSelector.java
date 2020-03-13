package com.ximuyi.core.bt;

import java.util.List;

/**
 * 或者
 * @param <T>
 */
public class BTNodeSelector<T extends BTContext> extends BTNodeBond<T> {

    @Override
    public boolean enableRun(T context) {
        List<BTNode<T>> children = getChildren();
        for (BTNode<T> btNode : children) {
            if (btNode.run(context)){
                return true;
            }
        }
        return false;
    }
}
