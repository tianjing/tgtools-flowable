package com.github.tianjing.tgtools.flowable.autoconfigure.controller;

import org.flowable.idm.api.User;
import org.flowable.ui.common.security.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 登录接口，界面ui 需要这个接口
 *
 * @author 田径
 * @create 2019-07-30 8:53
 * @desc
 **/
@Controller
@RequestMapping("/data/modeler/")
public class UserAccountController {


    @ResponseBody
    @RequestMapping(value = "account", method = RequestMethod.GET)
    public User account() {
        return SecurityUtils.getCurrentUserObject();
    }
}
