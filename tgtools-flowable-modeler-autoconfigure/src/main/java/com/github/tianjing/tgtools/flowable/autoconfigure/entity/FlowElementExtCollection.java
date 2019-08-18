package com.github.tianjing.tgtools.flowable.autoconfigure.entity;

import org.flowable.bpmn.model.FlowElement;
import tgtools.exceptions.APPErrorException;
import tgtools.json.JSONArray;
import tgtools.json.JSONException;
import tgtools.json.JSONObject;
import tgtools.util.RegexHelper;
import tgtools.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tian_ on 2016-09-23.
 */
public class FlowElementExtCollection extends ArrayList<FlowElementExt> {
    public FlowElementExtCollection() {
    }

    public <T extends FlowElement> FlowElementExtCollection(List<T> pFlowElements) {
        addAll(pFlowElements);
    }

    public <T extends FlowElement> void addAll(List<T> pFlowElements) {
        for (int i = 0; i < pFlowElements.size(); i++) {
            this.add(new FlowElementExt(pFlowElements.get(i)));
        }
    }

    /**
     * @return
     */
    public JSONArray toJSONArray() throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < this.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("id", this.get(i).getID());
            json.put("name", this.get(i).getName());
            json.put("key", this.get(i).getKey());
            json.put("value", getValue(this.get(i).getValue()));
            json.put("isGroup", this.get(i).getIsGroup());
            array.put(json);
        }
        return array;
    }

    /**
     * 返回下个节点的变量反值的集合
     * 比如 $｛pass==false｝ 那么 map 里就是 pass true
     *
     * @return
     * @throws APPErrorException
     */
    public Map<String, Object> toNegativeVariables() throws APPErrorException {
        Map<String, Object> var = toVariables();
        for (String key : var.keySet()) {
            if (var.get(key) instanceof Boolean) {
                var.put(key, !((Boolean) var.get(key)));
            } else if (var.get(key) instanceof Integer) {
                var.put(key, Integer.MAX_VALUE);
            }
        }
        return var;
    }

    /**
     * 返回下个节点的变量的集合
     *
     * @return
     * @throws APPErrorException
     */
    public Map<String, Object> toVariables() throws APPErrorException {
        Map<String, Object> vars = new HashMap<String, Object>();
        for (int i = 0; i < this.size(); i++) {
//            if (vars.containsKey(this.get(i).getKey())) {
//                throw new APPErrorException("表达式有重复的变量");
//            }
            if (!StringUtil.isNullOrEmpty(this.get(i).getKey())) {
                vars.put(this.get(i).getKey(), getValue(this.get(i).getValue()));
            }
        }
        return vars;
    }

    private Object getValue(String pValue) {
        if (StringUtil.isNullOrEmpty(pValue)) {
            return pValue;
        } else if (Boolean.TRUE.toString().equals(pValue) || Boolean.FALSE.toString().equals(pValue)) {
            return Boolean.valueOf(pValue);
        } else if (RegexHelper.isNubmer(pValue)) {
            return Integer.valueOf(pValue);
        }
        return pValue;
    }
}
