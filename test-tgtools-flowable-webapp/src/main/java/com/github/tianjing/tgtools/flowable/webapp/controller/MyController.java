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
import tgtools.exceptions.APPErrorException;

import java.util.HashMap;

/**
 * @author 田径
 * @create 2019-08-05 9:12
 * @desc
 **/
@Controller
@RequestMapping("/data/modeler1/")
public class MyController {
    public MyController()
    {
        System.out.println("");
    }
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
    String modelId = "1bf5f9dd-dec5-11ea-a29b-a86daa000424";//1bf5f9dd-dec5-11ea-a29b-a86daa000424
    String flowId = "bbbb:2:f95a58fb-dec6-11ea-81c1-a86daa000424";

    @ResponseBody
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public User account() {
        return SecurityUtils.getCurrentUserObject();
    }
    @ResponseBody
    @RequestMapping(value = "/deloy", method = RequestMethod.GET)
    public String deloy() throws APPErrorException {
        flowService.deloy(modelId);
        return "1";
    }
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.GET)
    public String start() {
        String vBussionId = "2321fdsafdsfdsfas";
        flowService.startFlow(flowId, vBussionId, new HashMap());
        return "1";
    }
    @ResponseBody
    @RequestMapping(value = "/image", method = RequestMethod.GET,produces={"image/png"})
    public byte[] image() {
        String vBussionId = "2321fdsafdsfdsfas";
        byte[] data = new byte[0];
        try {
            data = flowService.getHistoryHighlightImg(vBussionId);
        } catch (APPErrorException e) {
            e.printStackTrace();
        }
        return data;
    }
}
