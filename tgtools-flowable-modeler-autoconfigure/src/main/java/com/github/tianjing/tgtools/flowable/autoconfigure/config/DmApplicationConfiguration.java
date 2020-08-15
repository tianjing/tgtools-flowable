package com.github.tianjing.tgtools.flowable.autoconfigure.config;


import org.flowable.ui.modeler.conf.ApplicationConfiguration;
import org.flowable.ui.modeler.conf.JacksonConfiguration;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.flowable.ui.modeler.servlet.ApiDispatcherServletConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 应用启动加载和扫描的内容
 *
 * 取消了 需要的功能
 *         //  "org.flowable.ui.modeler.conf" ,
 *         //, "org.flowable.ui.modeler.security"
 *         //, "org.flowable.ui.common.conf"
 *         //, "org.flowable.ui.common.service"
 *         //, "org.flowable.ui.common.security"
 *         // , "com.github.tianjing.tgtools.flowable.autoconfigure.controller"
 * @author 田径
 * @create 2019-07-28 15:13
 * @desc
 **/
@Configuration
@EnableConfigurationProperties({FlowableModelerAppProperties.class})
@Import(value = {JacksonConfiguration.class, DmDatabaseConfiguration.class})
@ComponentScan(basePackages = {
        "com.github.tianjing.tgtools.flowable.autoconfigure.service"
        , "com.github.tianjing.tgtools.flowable.autoconfigure.controller"
        , "org.flowable.ui.modeler.repository"
        , "org.flowable.ui.modeler.service"
        , "org.flowable.ui.common.filter"
        , "org.flowable.ui.common.repository"
        , "org.flowable.ui.common.tenant"}
        , excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ApplicationConfiguration.class, org.flowable.ui.modeler.conf.DatabaseConfiguration.class})
)
public class DmApplicationConfiguration {

    public DmApplicationConfiguration() {
    }

    @Bean
    public ServletRegistrationBean modelerApiServlet(ApplicationContext applicationContext) {
        AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
        dispatcherServletConfiguration.setParent(applicationContext);
        dispatcherServletConfiguration.register(new Class[]{ApiDispatcherServletConfiguration.class});
        DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, new String[]{"/api/*"});
        registrationBean.setName("Flowable Modeler App API Servlet");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setAsyncSupported(true);
        return registrationBean;
    }
}
