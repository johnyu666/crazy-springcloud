package com.crazymaker.springcloud.back.end.start;


import com.crazymaker.springcloud.standard.config.TokenFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {
        "com.crazymaker.springcloud.back.end",
        "com.crazymaker.springcloud.seckill.remote.fallback",
        "com.crazymaker.springcloud.standard"
}, exclude = {SecurityAutoConfiguration.class})
@EnableScheduling
@EnableSwagger2
@EnableJpaRepositories(basePackages = {
        "com.crazymaker.springcloud.back.end.dao",
        "com.crazymaker.springcloud.base.dao"})

//@EnableRedisRepositories(basePackages = {
//        "com.crazymaker.springcloud.user.*.redis"})

@EntityScan(basePackages = {
        "com.crazymaker.springcloud.back.end.dao.po",
        "com.crazymaker.springcloud.base.dao.po",
        "com.crazymaker.springcloud.standard.*.dao.po"})
//启动Feign
@EnableFeignClients(basePackages =
        {"com.crazymaker.springcloud.seckill.remote.client"},
        defaultConfiguration = {TokenFeignConfiguration.class}
)

@EnableHystrix
@EnableCircuitBreaker
public class BackendCloudApplication
{


    public static void main(String[] args)
    {
        SpringApplication.run(BackendCloudApplication.class, args);
    }

}
