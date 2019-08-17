package com.github.tianjing.tgtools.flowable.webapp;

import com.github.tianjing.tgtools.flowable.autoconfigure.config.DmApplicationConfiguration;
import com.github.tianjing.tgtools.flowable.autoconfigure.config.FlowableConfig;
import org.flowable.spring.boot.FlowableJpaAutoConfiguration;
import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.flowable.spring.boot.ProcessEngineAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineAutoConfiguration;
import org.flowable.spring.boot.cmmn.CmmnEngineServicesAutoConfiguration;
import org.flowable.spring.boot.content.ContentEngineAutoConfiguration;
import org.flowable.spring.boot.content.ContentEngineServicesAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineAutoConfiguration;
import org.flowable.spring.boot.dmn.DmnEngineServicesAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineAutoConfiguration;
import org.flowable.spring.boot.form.FormEngineServicesAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineAutoConfiguration;
import org.flowable.spring.boot.idm.IdmEngineServicesAutoConfiguration;
import org.flowable.spring.boot.ldap.FlowableLdapAutoConfiguration;
import org.flowable.ui.modeler.servlet.AppDispatcherServletConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author 田径
 * @create 2019-07-28 12:31
 * @desc
 **/
@EnableAspectJAutoProxy(proxyTargetClass=true)
@Import({DmApplicationConfiguration.class, AppDispatcherServletConfiguration.class
        , FlowableConfig.class})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class
        , FlowableSecurityAutoConfiguration.class
        , FlowableLdapAutoConfiguration.class
        , CmmnEngineServicesAutoConfiguration.class
        , CmmnEngineAutoConfiguration.class
        , IdmEngineServicesAutoConfiguration.class
        , IdmEngineAutoConfiguration.class
        , DmnEngineServicesAutoConfiguration.class
        , DmnEngineAutoConfiguration.class
        , ContentEngineServicesAutoConfiguration.class
        , ContentEngineAutoConfiguration.class
        , FormEngineServicesAutoConfiguration.class
        , FormEngineAutoConfiguration.class
        , FlowableJpaAutoConfiguration.class
        , ProcessEngineAutoConfiguration.class
})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


}
