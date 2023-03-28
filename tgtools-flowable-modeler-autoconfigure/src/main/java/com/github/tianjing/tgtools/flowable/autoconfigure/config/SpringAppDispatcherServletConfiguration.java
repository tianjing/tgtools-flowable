package com.github.tianjing.tgtools.flowable.autoconfigure.config;

import org.flowable.ui.modeler.servlet.AppDispatcherServletConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


@Configuration
@ComponentScan({"org.flowable.ui.modeler.rest.app", "org.flowable.ui.common.rest"})
@EnableAsync
public class SpringAppDispatcherServletConfiguration  implements WebMvcRegistrations {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAppDispatcherServletConfiguration.class);

//    @Bean
//    public SessionLocaleResolver localeResolver() {
//        return new SessionLocaleResolver();
//    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LOGGER.debug("Configuring localeChangeInterceptor");
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        LOGGER.debug("Creating requestMappingHandlerMapping");
        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
        requestMappingHandlerMapping.setRemoveSemicolonContent(false);
        Object[] interceptors = new Object[]{this.localeChangeInterceptor()};
        requestMappingHandlerMapping.setInterceptors(interceptors);
        return requestMappingHandlerMapping;
    }
}