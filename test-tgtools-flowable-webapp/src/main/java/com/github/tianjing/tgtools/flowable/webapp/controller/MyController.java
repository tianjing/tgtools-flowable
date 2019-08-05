//package com.github.tianjing.tgtools.flowable.webapp.controller;
//
//import org.flowable.engine.*;
//import org.flowable.idm.api.User;
//import org.flowable.ui.common.security.SecurityUtils;
//import org.flowable.ui.modeler.serviceapi.ModelService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
// * @author 田径
// * @create 2019-08-05 9:12
// * @desc
// **/
//@Controller
//@RequestMapping("/data/modeler1/")
//public class MyController {
//
//    @Autowired
//    public RepositoryService repositoryService;
//
//    @Autowired
//    public RuntimeService runtimeService;
//    @Autowired
//    public TaskService taskService;
//    @Autowired
//    public HistoryService historyService;
//
//
//
//    @Autowired
//    public IdentityService identityService;
//
//
//
//    @Autowired
//    public DynamicBpmnService dynamicBpmnService;
//
//    @Autowired
//    ModelService modelService;
//
//    @Autowired
//    ApplicationContext applicationContext;
//
//    @ResponseBody
//    @RequestMapping(value = "account", method = RequestMethod.GET)
//    public User account() {
//        return SecurityUtils.getCurrentUserObject();
//    }
//}
