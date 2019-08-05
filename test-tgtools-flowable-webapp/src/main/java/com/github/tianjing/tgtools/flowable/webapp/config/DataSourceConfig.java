package com.github.tianjing.tgtools.flowable.webapp.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author 田径
 * @create 2019-07-30 21:02
 * @desc
 **/
@Configuration
public class DataSourceConfig {

    @Bean({"myDataSource"})
    @ConfigurationProperties(
            prefix = "spring.datasource.dev"
    )
    public DataSource myDataSource() {
        DruidDataSource vDatasource = new DruidDataSource();
        //(new TgtoolsConfig()).loadDataSource("MyDATAACCESS",vDatasource);
        return vDatasource;
    }

}
