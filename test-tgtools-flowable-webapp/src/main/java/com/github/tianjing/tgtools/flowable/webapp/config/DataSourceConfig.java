package com.github.tianjing.tgtools.flowable.webapp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author 田径
 * @create 2019-07-30 21:02
 * @desc
 **/
@Configuration
public class DataSourceConfig {

    @Bean({"flowableDataSource"})
    @ConfigurationProperties(
            prefix = "spring.datasource.dev"
    )
    public DataSource myDataSource() {
        HikariDataSource vDatasource = new HikariDataSource();
        return vDatasource;
    }

}
