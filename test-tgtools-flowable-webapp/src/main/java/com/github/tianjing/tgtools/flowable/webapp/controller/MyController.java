package com.github.tianjing.tgtools.flowable.webapp.controller;

import com.github.tianjing.tgtools.flowable.autoconfigure.service.FlowService;
import org.flowable.engine.*;
import org.flowable.idm.api.User;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.modeler.repository.ModelRelationRepository;
import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * @author 田径
 * @create 2019-08-05 9:12
 * @desc
 **/
@Controller
@RequestMapping("/data/modeler1/")
public class MyController {

    @Autowired
    public RepositoryService repositoryService;

    @Autowired
    public RuntimeService runtimeService;
    @Autowired
    public TaskService taskService;
    @Autowired
    public HistoryService historyService;

    @Autowired
    public IdentityService identityService;

    @Autowired
    public DynamicBpmnService dynamicBpmnService;
    @Autowired
    protected FlowableModelQueryService modelQueryService;
    @Autowired
    ModelService modelService;
    @Autowired
    ModelRelationRepository modelRelationRepository;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    FlowService flowService;
    String modelId = "063284b9-b207-11e9-887b-ac2b6e2536bd";
    String flowId = "fdadf:1:eec8175f-c0fd-11e9-aaff-ac2b6e2536bd";

    @ResponseBody
    @RequestMapping(value = "account", method = RequestMethod.GET)
    public User account() {
        return SecurityUtils.getCurrentUserObject();
    }

    @RequestMapping(value = "deloy", method = RequestMethod.GET)
    public String deloy() {
        flowService.deloy(modelId);
        return "1";
    }

    @RequestMapping(value = "start", method = RequestMethod.GET)
    public String start() {
        String vBussionId = "";
        flowService.startFlow(flowId, vBussionId, new HashMap());
        return "1";
    }
}
