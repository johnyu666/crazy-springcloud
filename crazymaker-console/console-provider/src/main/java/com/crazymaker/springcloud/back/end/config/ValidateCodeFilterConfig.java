package com.crazymaker.springcloud.back.end.config;

import com.crazymaker.springcloud.back.end.security.filter.ValidateCodeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class ValidateCodeFilterConfig
{

    @Resource
    private ValidateCodeFilter validateCodeFilter;

    @Bean
    public FilterRegistrationBean buildSessionPrefixFilter()
    {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setOrder(0);
        filterRegistrationBean.setFilter(validateCodeFilter);
        filterRegistrationBean.setName("validateCodeFilter" );
        filterRegistrationBean.addUrlPatterns("/*" );
        return filterRegistrationBean;
    }


}