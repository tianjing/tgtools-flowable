package com.github.tianjing.tgtools.flowable.webapp.config;

import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.flowable.entitylink.api.history.HistoricEntityLinkService;
import org.flowable.identitylink.service.HistoricIdentityLinkService;
import org.flowable.task.service.HistoricTaskService;
import org.flowable.variable.service.HistoricVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 田径
 * @date 2021-07-28 9:56
 * @desc
 **/
@Configuration
public class TestRunnerConfig {

    @Bean
    public TestRunner TestRunner() {
        return new TestRunner();
    }

    public static class TestRunner implements ApplicationRunner {
        @Autowired
        TaskService taskService;

        @Autowired
        IdentityService identityService;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            taskService.createTaskQuery().taskCandidateUser("1").list();
            identityService.createUserQuery().tenantId("1").list();
        }
    }

}
