package com.github.tianjing.tgtools.flowable.autoconfigure.config;

import com.github.tianjing.tgtools.flowable.db.dm.ext.DmAppEngineConfigurationImpl;
import com.github.tianjing.tgtools.flowable.db.dm.ext.DmProcessEngineConfigurationImpl;
import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author 田径
 * @create 2019-07-28 11:48
 * @desc
 **/
@Configuration
public class FlowableConfig {

    @Autowired
    protected PlatformTransactionManager platformTransactionManager;
    @Autowired
    private DataSource dataSource;



    @Bean
    public SpringAppEngineConfiguration springAppEngineConfiguration() {
        DmAppEngineConfigurationImpl conf = new DmAppEngineConfigurationImpl();
        //设置数据源
        conf.setDataSource(dataSource);
        //自动更新表
        conf.setDatabaseSchemaUpdate("true");
        //达梦7
        conf.setDatabaseType("dm");

        //支持事物
        conf.setTransactionManager(platformTransactionManager);

        conf.setIdGenerator(new StrongUuidGenerator());
        return conf;
    }

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration() {
        DmProcessEngineConfigurationImpl configuration = new DmProcessEngineConfigurationImpl();
        configuration.setActivityFontName("宋体");
        configuration.setLabelFontName("宋体");
        configuration.setAnnotationFontName("宋体");

        //设置数据源
        configuration.setDataSource(dataSource);
        //自动更新表
        configuration.setDatabaseSchemaUpdate("true");
        //达梦7
        configuration.setDatabaseType("dm");

        //使用guid 主键
        configuration.setIdGenerator(new StrongUuidGenerator());
        //支持事物
        configuration.setTransactionManager(platformTransactionManager);
        return configuration;
    }

    /**
     * 流程引擎，与spring整合使用factoryBean
     *
     * @param processEngineConfiguration
     * @return
     */
    @Bean
    public ProcessEngineFactoryBean processEngine(ProcessEngineConfiguration processEngineConfiguration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);
        return processEngineFactoryBean;
    }

}