package com.github.tianjing.tgtools.flowable.autoconfigure.entity;


import org.flowable.bpmn.model.*;
import org.springframework.util.StringUtils;
import tgtools.util.StringUtil;

/**
 * Created by tian_ on 2016-09-23.
 */
public class FlowElementExt {
    private String id;
    private String name;
    private String key;
    private String value;
    private boolean isGroup = false;
    private FlowElement flowElement;
    private boolean isParse = false;

    public FlowElementExt(FlowElement pFlowElement) {
        flowElement = pFlowElement;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    private void parseConditionText() {
        if (isParse) {
            return;
        }

        if (flowElement instanceof SequenceFlow) {
            SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
            String conditionText = sequenceFlow.getConditionExpression();
            if (!StringUtils.isEmpty(conditionText)) {
                if (conditionText.contains("$")) {
                    conditionText = conditionText.trim();
                    conditionText = conditionText.substring(2, conditionText.length() - 1);
                    String[] strs = conditionText.split("==");
                    key = strs[0].trim();
                    value = strs[1].trim();
                }
            }
        }
        isParse = true;
    }

    public String getID() {
        return flowElement.getId();
    }


    public String getName() {
        //endEvent
        Object type = flowElement.getClass().getName().toLowerCase();
        if (null != type && "endevent".equals(type.toString())) {
            return "结束";
        }
        return getName(flowElement);
    }

    private String getName(FlowElement pFlowElement) {
        if (pFlowElement instanceof SequenceFlow) {
            SequenceFlow sequenceFlow = (SequenceFlow) pFlowElement;
            if (sequenceFlow.getTargetFlowElement() instanceof UserTask) {
                String name = sequenceFlow.getTargetFlowElement().getName();
                if (null != name && !StringUtil.isNullOrEmpty(name.toString())) {
                    if ((sequenceFlow.getSourceFlowElement() instanceof ParallelGateway) || (sequenceFlow.getSourceFlowElement() instanceof UserTask)) {
                        isGroup = true;
                    }
                    return name;
                }
            }
            return getName(((FlowNode) sequenceFlow.getTargetFlowElement()).getOutgoingFlows().get(0));
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getKey() {
        parseConditionText();
        return key;
    }

    public String getValue() {
        parseConditionText();
        return value;
    }


    public FlowElement getFlowElement() {
        return flowElement;
    }


}
