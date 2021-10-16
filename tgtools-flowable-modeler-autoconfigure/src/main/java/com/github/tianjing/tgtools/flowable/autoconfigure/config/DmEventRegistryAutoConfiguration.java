package com.github.tianjing.tgtools.flowable.autoconfigure.config;

import org.flowable.common.spring.AutoDeploymentStrategy;
import org.flowable.eventregistry.api.ChannelModelProcessor;
import org.flowable.eventregistry.api.management.EventRegistryChangeDetectionExecutor;
import org.flowable.eventregistry.impl.EventRegistryEngine;
import org.flowable.eventregistry.spring.SpringEventRegistryEngineConfiguration;
import org.flowable.spring.boot.FlowableAutoDeploymentProperties;
import org.flowable.spring.boot.FlowableProperties;
import org.flowable.spring.boot.eventregistry.EventRegistryAutoConfiguration;
import org.flowable.spring.boot.eventregistry.FlowableEventRegistryProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * @author 田径
 * @date 2021-10-16 10:41
 * @desc
 **/
public class DmEventRegistryAutoConfiguration extends EventRegistryAutoConfiguration {
    public DmEventRegistryAutoConfiguration(FlowableProperties flowableProperties, FlowableEventRegistryProperties eventProperties, FlowableAutoDeploymentProperties autoDeploymentProperties) {
        super(flowableProperties, eventProperties, autoDeploymentProperties);
    }

    @Override
    @Bean
    @ConditionalOnMissingBean
    public SpringEventRegistryEngineConfiguration eventEngineConfiguration(@Qualifier("flowableDataSource") DataSource dataSource,
                                                                           PlatformTransactionManager platformTransactionManager,
                                                                           ObjectProvider<List<ChannelModelProcessor>> channelModelProcessors,
                                                                           ObjectProvider<List<AutoDeploymentStrategy<EventRegistryEngine>>> eventAutoDeploymentStrategies,
                                                                           ObjectProvider<TaskScheduler> taskScheduler,
                                                                           ObjectProvider<EventRegistryChangeDetectionExecutor> eventRegistryChangeDetectionExecutor) throws IOException {
        return super.eventEngineConfiguration(dataSource, platformTransactionManager, channelModelProcessors, eventAutoDeploymentStrategies, taskScheduler, eventRegistryChangeDetectionExecutor);
    }
}