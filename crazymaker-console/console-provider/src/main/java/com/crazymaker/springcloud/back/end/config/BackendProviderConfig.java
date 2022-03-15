package com.crazymaker.springcloud.back.end.config;

import com.crazymaker.springcloud.back.end.service.impl.RedisAuthorizationCodeServices;
import com.crazymaker.springcloud.back.end.service.impl.RedisClientDetailsService;
import com.crazymaker.springcloud.common.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author zlt
 * @date 2018/12/12
 */
@Configuration
public class BackendProviderConfig
{

    @Resource
    private ObjectMapper objectMapper;
    /**
     * 登陆失败，返回401
     */
    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            String msg = exception.getMessage();
            ResponseUtil.responseFailed(objectMapper,
                    response,
                    msg,
                    HttpStatus.UNAUTHORIZED.value());
        };
    }

}
