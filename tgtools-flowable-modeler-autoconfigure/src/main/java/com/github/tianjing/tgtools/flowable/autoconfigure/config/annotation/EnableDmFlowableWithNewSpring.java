package com.github.tianjing.tgtools.flowable.autoconfigure.config.annotation;

import com.github.tianjing.tgtools.flowable.autoconfigure.config.*;
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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@ImportAutoConfiguration(
        value = {DmApplicationConfiguration.class,
                SpringAppDispatcherServletConfiguration.class,
                FlowableConfig.class,
                DmDataSourceTransactionManagerAutoConfiguration.class,
                DmEventRegistryAutoConfiguration.class},

        exclude = {SecurityAutoConfiguration.class
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
        }
)
public @interface EnableDmFlowableWithNewSpring {
}
