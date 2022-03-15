package com.crazymaker.springcloud.back.end.service.impl;

import com.crazymaker.springcloud.standard.redis.RedisRepository;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

/**
 * @author zlt
 * JdbcAuthorizationCodeServices替换
 */
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices
{

    /**
     * 替换JdbcAuthorizationCodeServices的存储策略
     * 将存储code到redis，并设置过期时间，10分钟
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication)
    {
        RedisRepository.singleton().setExpire(redisKey(code), authentication, 10 * 60);
    }

    @Override
    protected OAuth2Authentication remove(final String code)
    {
        String codeKey = redisKey(code);
        OAuth2Authentication token = (OAuth2Authentication) RedisRepository.singleton().getObject(codeKey);
        RedisRepository.singleton().del(codeKey);
        return token;
    }

    /**
     * redis中 code key的前缀
     *
     * @param code
     * @return
     */
    private String redisKey(String code)
    {
        return "oauth:code:" + code;
    }
}
