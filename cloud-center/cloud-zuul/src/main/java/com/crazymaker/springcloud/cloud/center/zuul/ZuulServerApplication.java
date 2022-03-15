package com.crazymaker.springcloud.cloud.center.zuul;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.crazymaker.springcloud.base.config.RedisSessionFilterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(
        scanBasePackages = {
                "com.crazymaker.springcloud.cloud.center.zuul",
                "com.crazymaker.springcloud.standard",
                "com.crazymaker.springcloud.base",
                "com.crazymaker.springcloud.user.info.contract"
        },

        exclude = {
                SecurityAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                DruidDataSourceAutoConfigure.class,
                SecurityFilterAutoConfiguration.class,
                RedisSessionFilterConfig.class
        })

@EnableScheduling
@EnableHystrix
@EnableDiscoveryClient

//声明一个Zuul服务
@EnableZuulProxy
@EnableCircuitBreaker
public class ZuulServerApplication
{

    public static void main(String[] args)
    {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(ZuulServerApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");

        System.out.println("\n----------------------------------------------------------\n\t" +
                "Zuul 内部网关 is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "swagger-ui: \thttp://localhost:" + port + "/swagger-ui.html\n\t" +
                "----------------------------------------------------------");
    }

}


