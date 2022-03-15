package com.crazymaker.springcloud.back.end.config;

import com.crazymaker.springcloud.back.end.service.impl.RedisAuthorizationCodeServices;
import com.crazymaker.springcloud.back.end.service.impl.RedisClientDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author zlt
 * @date 2018/12/12
 */
@Configuration
public class ClientDetailsConfig
{
    @Resource
    private DataSource dataSource;

    /**
     * 声明 ClientDetails实现
     */
    @Bean
    public RedisClientDetailsService clientDetailsService()
    {
        RedisClientDetailsService clientDetailsService =
                new RedisClientDetailsService(dataSource);
        return clientDetailsService;
    }

    @Bean
    public RandomValueAuthorizationCodeServices authorizationCodeServices()
    {
        RedisAuthorizationCodeServices redisAuthorizationCodeServices = new RedisAuthorizationCodeServices();
        return redisAuthorizationCodeServices;
    }
}
