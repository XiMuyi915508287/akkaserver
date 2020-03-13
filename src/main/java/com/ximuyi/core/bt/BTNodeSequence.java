package com.ximuyi.core.bt;

/**
 * 并且
 * 1. 1-(n-1)节点之前，做预备操作，n节点做实际操作
 * 2. 一直做某个动作，直到某一个动作失败
 * @param <T>
 */
public class BTNodeSequence<T extends BTContext> extends BTNodeBond<T> {

    @Override
    public boolean enableRun(T context) {
        for (BTNode<T> btNode : getChildren()) {
            if (!btNode.run(context)){
                return false;
            }
        }
        return true;
    }
}
