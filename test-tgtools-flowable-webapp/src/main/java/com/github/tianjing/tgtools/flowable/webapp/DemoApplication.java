package com.github.tianjing.tgtools.flowable.webapp;

import com.github.tianjing.tgtools.flowable.autoconfigure.config.annotation.EnableDmFlowable;
import com.github.tianjing.tgtools.flowable.autoconfigure.config.annotation.EnableDmFlowableWithNewSpring;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 田径
 * @create 2019-07-28 12:31
 * @desc
 **/
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableDmFlowableWithNewSpring
@SpringBootApplication
public class DemoApplication {
    ;

    //DataSourceTransactionManagerAutoConfiguration;
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


}
