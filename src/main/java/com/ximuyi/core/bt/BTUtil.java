package com.ximuyi.core.bt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class BTUtil {
    public static final int RANDOM = 1000;
    public static final int WEIGHT = 1;

    public static int getWeight(JSONObject params){
        return params.containsKey("weight") ? params.getInteger("weight") : WEIGHT;
    }

    public static JSONArray getChildren(JSONObject params){
        return params.getJSONArray("children");
    }

    public static JSONObject getCondition(JSONObject params){
        return params.containsKey("condition") ? params.getJSONObject("condition") : null;
    }

    public static JSONObject getDecorator(JSONObject params){
        return params.containsKey("decorator") ? params.getJSONObject("decorator") : null;
    }

    public static String getType(JSONObject params){
        return params.getString("type");
    }

    public static int getRandom(JSONObject params){
        return params.containsKey("random") ? params.getInteger("random") : RANDOM;
    }

}
