package com.ximuyi.core.bt;

import com.alibaba.fastjson.JSONObject;

public abstract class BTNode<T extends BTContext> {

    private BTNode<T> condition = null;
    private int weight = BTUtil.WEIGHT;

    protected void parse(JSONObject params){
        this.weight = BTUtil.getWeight(params);
    }

    public final int getWeight(){
        return weight;
    }

    public final boolean run(T context){
        if (condition == null || condition.run(context)){
            return enableRun(context);
        }
        else {
            return false;
        }
    }

    public abstract boolean enableRun(T context);

    protected final void setCondition(BTNode<T> condition) {
        this.condition = condition;
    }
}
