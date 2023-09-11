package com.github.tianjing.tgtools.flowable.autoconfigure.config;

import com.github.tianjing.tgtools.flowable.autoconfigure.idm.DmIdmDbSchemaManager;
import com.github.tianjing.tgtools.flowable.db.dm.ext.DmAppEngineConfigurationImpl;
import com.github.tianjing.tgtools.flowable.db.dm.ext.DmProcessEngineConfigurationImpl;
import org.flowable.app.spring.SpringAppEngineConfiguration;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.idm.engine.configurator.IdmEngineConfigurator;
import org.flowable.idm.engine.impl.cfg.StandaloneIdmEngineConfiguration;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import tgtools.util.StringUtil;

import javax.sql.DataSource;

/**
 * @author 田径
 * @create 2019-07-28 11:48
 * @desc
 **/
@Configuration
public class FlowableConfig {

    @Value("${flowable.database-schema-update:true}")
    private String databaseSchemaUpdate;
    @Value("${flowable.database-table-prefix:}")
    private String databaseTablePrefix;

    @Autowired
    protected PlatformTransactionManager platformTransactionManager;
    @Autowired
    @Qualifier("flowableDataSource")
    private DataSource dataSource;


    @ConfigurationProperties(prefix = "flowable.app.config")
    @Bean
    public SpringAppEngineConfiguration springAppEngineConfiguration(StandaloneIdmEngineConfiguration standaloneIdmEngineConfiguration) {
        DmAppEngineConfigurationImpl conf = new DmAppEngineConfigurationImpl();
        //设置数据源
        conf.setDataSource(dataSource);
        //自动更新表
        conf.setDatabaseSchemaUpdate(databaseSchemaUpdate);
        //达梦7
        conf.setDatabaseType("dm");
        //支持事物
        conf.setTransactionManager(platformTransactionManager);

        if (StringUtil.isNotEmpty(databaseTablePrefix)) {
            conf.setDatabaseTablePrefix(databaseTablePrefix);
        }

        IdmEngineConfigurator vIdmEngineConfigurator = new IdmEngineConfigurator();
        vIdmEngineConfigurator.setIdmEngineConfiguration(standaloneIdmEngineConfiguration);
        conf.setIdmEngineConfigurator(vIdmEngineConfigurator);

        conf.setIdGenerator(new StrongUuidGenerator());
        return conf;
    }


    @ConfigurationProperties(prefix = "flowable.engine.config")
    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(StandaloneIdmEngineConfiguration standaloneIdmEngineConfiguration) {
        DmProcessEngineConfigurationImpl configuration = new DmProcessEngineConfigurationImpl();
        configuration.setActivityFontName("宋体");
        configuration.setLabelFontName("宋体");
        configuration.setAnnotationFontName("宋体");
        //configuration.setDatabaseTablePrefix("FLOWABLE6_5.");
        //设置数据源
        configuration.setDataSource(dataSource);
        //自动更新表
        configuration.setDatabaseSchemaUpdate(databaseSchemaUpdate);
        //达梦7
        configuration.setDatabaseType("dm");

        //使用guid 主键
        configuration.setIdGenerator(new StrongUuidGenerator());
        //支持事物
        configuration.setTransactionManager(platformTransactionManager);

        if (StringUtil.isNotEmpty(databaseTablePrefix)) {
            configuration.setDatabaseTablePrefix(databaseTablePrefix);
        }

        IdmEngineConfigurator vIdmEngineConfigurator = new IdmEngineConfigurator();
        vIdmEngineConfigurator.setIdmEngineConfiguration(standaloneIdmEngineConfiguration);
        configuration.setIdmEngineConfigurator(vIdmEngineConfigurator);

        return configuration;
    }

    @ConfigurationProperties(prefix = "flowable.idm.config")
    @Bean
    public StandaloneIdmEngineConfiguration standaloneIdmEngineConfiguration() {
        StandaloneIdmEngineConfiguration configuration = new StandaloneIdmEngineConfiguration();
        //设置数据源
        configuration.setDataSource(dataSource);
        //自动更新表
        configuration.setDatabaseSchemaUpdate(databaseSchemaUpdate);
        //达梦7
        configuration.setDatabaseType("dm");
        if (StringUtil.isNotEmpty(databaseTablePrefix)) {
            configuration.setDatabaseTablePrefix(databaseTablePrefix);
        }

        configuration.setSchemaManager(new DmIdmDbSchemaManager(configuration));

        //使用guid 主键
        configuration.setIdGenerator(new StrongUuidGenerator());
        return configuration;
    }

    @Bean
    public DmIdmDbSchemaManager dmIdmDbSchemaManager(StandaloneIdmEngineConfiguration standaloneIdmEngineConfiguration) {
        DmIdmDbSchemaManager configuration = new DmIdmDbSchemaManager(standaloneIdmEngineConfiguration);

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

    @ConfigurationProperties(prefix = "flowable.modeler.app")
    @Bean
    public FlowableModelerAppProperties flowableModelerAppProperties() {
        FlowableModelerAppProperties conf = new FlowableModelerAppProperties();
        if (StringUtil.isNotEmpty(databaseTablePrefix)) {
            conf.setDataSourcePrefix(databaseTablePrefix);
        }

        return conf;
    }
}