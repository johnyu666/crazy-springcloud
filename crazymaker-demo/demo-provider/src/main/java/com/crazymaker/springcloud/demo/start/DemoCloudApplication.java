package com.crazymaker.springcloud.demo.start;


import com.crazymaker.springcloud.standard.config.FeignConfiguration;
import com.crazymaker.springcloud.standard.context.AppContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
@SpringBootApplication(scanBasePackages =
        {
                "com.crazymaker.springcloud.demo",
                "com.crazymaker.springcloud.base",
                "com.crazymaker.springcloud.user",
                "com.crazymaker.springcloud.seckill.remote.fallback",
                "com.crazymaker.springcloud.standard"
        }, exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@EnableSwagger2
@EnableJpaRepositories(basePackages = {
//        "com.crazymaker.springcloud.user.*.dao",
        "com.crazymaker.springcloud.base.dao"
})


@EntityScan(basePackages = {
        "com.crazymaker.springcloud.user.*.dao.po",
        "com.crazymaker.springcloud.base.dao.po",
        "com.crazymaker.springcloud.standard.*.dao.po"})
/**
 * 启用 Hystrix
 */
@EnableHystrix
@EnableFeignClients(
        basePackages = "com.crazymaker.springcloud.user.info.remote.client",
        defaultConfiguration = FeignConfiguration.class)
@EnableTransactionManagement
@Slf4j
@EnableEurekaClient
public class DemoCloudApplication
{
    public static void main(String[] args)
    {
        ConfigurableApplicationContext applicationContext =   SpringApplication.run(DemoCloudApplication.class, args);

        /**
         * 打印所有的 spring ioc bean
         */
        List<String> beans = AppContextHolder.getBeanDefinitionNames();
        log.info(beans.toString());

        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        System.out.println("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path+ "/index.html\n\t" +
                "swagger-ui: \thttp://localhost:" + port + path + "/swagger-ui.html\n\t" +
                "----------------------------------------------------------");

    }

}
