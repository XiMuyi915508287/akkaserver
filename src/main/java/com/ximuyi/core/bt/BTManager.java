package com.ximuyi.core.bt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ximuyi.core.utils.ImmutableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BTManager<T extends BTContext> {

    private static final Logger logger = LoggerFactory.getLogger(BTManager.class);

    private Map<String, Class<? extends BTNode>> type2Cls;

    public BTManager() {
        this.type2Cls = Collections.emptyMap();
        this.add("Selector", BTNodeSelector.class);
        this.add("Sequence", BTNodeSequence.class);
        this.add("Decorator", BTNodeDecorator.class);
        this.add("Select", BTNodeSelector.class);
        this.add("True", BTNode_True.class);
        this.add("False", BTNode_False.class);
    }

    protected void add(String type, Class<? extends BTNode> cls){
        this.type2Cls = ImmutableUtil.add(type2Cls, type, cls);
    }

    protected BTNode<T> createNode(JSONObject params){
        String type = BTUtil.getType(params);
        Class<? extends BTNode> cls = type2Cls.get(type);
        BTNode<T> btNode = null;
        if (cls == null){
            btNode = createCustomNode(type);
        }
        else {
            try {
                btNode = cls.newInstance();
            } catch (Throwable e) {
                logger.error(cls.getName(), e);
                btNode = new BTNode_None<>();
            }
        }
        btNode.parse(params);
        JSONObject condition = BTUtil.getCondition(params);
        if (condition != null){
            btNode.setCondition(createNode(condition));
        }
        if (btNode instanceof BTNodeBond){
            JSONArray jsonArray = BTUtil.getChildren(params);
            List<BTNode<T>> children = new ArrayList<>(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                children.add(createNode(jsonArray.getJSONObject(i)));
            }
            BTNodeBond<T> btNodeList = (BTNodeBond<T>)btNode;
            btNodeList.addChildren(children);
        }
        else if (btNode instanceof BTNodeDecorator){
            BTNodeDecorator<T> decorator = (BTNodeDecorator<T>)btNode;
            decorator.setDecorator(createNode(BTUtil.getDecorator(params)));
        }
        return btNode;
    }

    protected abstract BTNode<T> createCustomNode(String type);
}
