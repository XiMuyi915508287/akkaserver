package com.ximuyi.core.bt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.ximuyi.core.utils.RandomUtil;

public abstract class BTNodeBond<T extends BTContext> extends BTNode<T> {

    private List<BTNode<T>> children = Collections.emptyList();
    private int random = BTUtil.RANDOM;

    @Override
    protected void parse(JSONObject params) {
        super.parse(params);
        random = BTUtil.getRandom(params);
    }

    public void addChildren(Collection<BTNode<T>> children){
        ImmutableList.Builder<BTNode<T>> builder = ImmutableList.builder();
        builder.addAll(this.children);
        builder.addAll(children);
        this.children = builder.build();
    }

    public void addChild(BTNode<T> child){
        ImmutableList.Builder<BTNode<T>> builder = ImmutableList.builder();
        builder.addAll(this.children);
        builder.add(child);
        children = builder.build();
    }

    public final List<BTNode<T>> getChildren() {
        if (random >= BTUtil.RANDOM){
            return children;
        }
        else if (RandomUtil.nextInt(BTUtil.RANDOM) < random){
            return RandomUtil.disorder(children);
        }
        else {
            return children;
        }
    }
}
