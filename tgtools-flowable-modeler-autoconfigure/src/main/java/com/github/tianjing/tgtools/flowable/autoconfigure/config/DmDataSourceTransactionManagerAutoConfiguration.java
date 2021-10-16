package com.github.tianjing.tgtools.flowable.autoconfigure.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author 田径
 * @date 2021-10-16 10:18
 * @desc
 **/

public class DmDataSourceTransactionManagerAutoConfiguration extends DataSourceTransactionManagerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({PlatformTransactionManager.class})
    DataSourceTransactionManager transactionManager(@Qualifier("flowableDataSource") DataSource dataSource, ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManagerCustomizers.ifAvailable((customizers) -> {
            customizers.customize(transactionManager);
        });
        return transactionManager;
    }
}
