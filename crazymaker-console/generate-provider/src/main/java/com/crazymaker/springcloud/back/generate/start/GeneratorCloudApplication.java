package com.crazymaker.springcloud.back.generate.start;


import com.crazymaker.springcloud.standard.config.TokenFeignConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {
        "com.crazymaker.springcloud.back.end",
        "com.crazymaker.springcloud.back.generate",
        "com.crazymaker.springcloud.seckill.remote.fallback",
        "com.crazymaker.springcloud.standard"
}, exclude = {SecurityAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class
})
@EnableScheduling
@EnableSwagger2
//@EnableJpaRepositories(basePackages = {
//        "com.crazymaker.springcloud.back.generate.dao",
//        "com.crazymaker.springcloud.back.end.dao",
//})

//@EnableRedisRepositories(basePackages = {
//        "com.crazymaker.springcloud.user.*.redis"})
//
//@EntityScan(basePackages = {
//        "com.crazymaker.springcloud.back.end.dao.po",
//        "com.crazymaker.springcloud.back.generate.dao.po",
//        "com.crazymaker.springcloud.standard.*.dao.po"})
//启动Feign
@EnableFeignClients(basePackages =
        {"com.crazymaker.springcloud.seckill.remote.client"},
        defaultConfiguration = {TokenFeignConfiguration.class}
)
@MapperScan({"com.crazymaker.springcloud.back.generate.mapper"})
@EnableHystrix
@EnableCircuitBreaker
public class GeneratorCloudApplication
{


    public static void main(String[] args)
    {
        SpringApplication.run(GeneratorCloudApplication.class, args);
    }

}
