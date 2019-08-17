package com.github.tianjing.tgtools.flowable.webapp.service;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author 田径
 * @create 2019-08-17 22:37
 * @desc
 **/
@Service
public class FlowService {

    @Autowired
    ModelService modelService;
    @Autowired
    RepositoryService repositoryService;

    /**
     * 发布流程
     *
     * @param pModelId
     * @return
     * @throws FlowableException
     */
    public Deployment deloy(String pModelId) throws FlowableException {
        Model modelData = modelService.getModel(pModelId);

        byte[] bytes = modelService.getBpmnXML(modelData);
        if (bytes == null) {
            throw new FlowableException("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }

        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().size() == 0) {
            throw new FlowableException("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        return repositoryService.createDeployment()
                .name(modelData.getName())
                .addBytes(processName, bpmnBytes)
                .deploy();
    }

    /**
     * 启动流程
     *
     * @param pFlowId
     * @param pBussionId
     * @param pVar
     */
    public void startFlow(String pFlowId, String pBussionId, HashMap pVar) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().startProcessInstanceById(pFlowId, pBussionId, pVar);
    }


}
