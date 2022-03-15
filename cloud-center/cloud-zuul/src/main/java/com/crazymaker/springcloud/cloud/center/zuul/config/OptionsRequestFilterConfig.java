package com.crazymaker.springcloud.cloud.center.zuul.config;

import com.crazymaker.springcloud.standard.filter.OptionsRequestFilter;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
public class OptionsRequestFilterConfig
{

    @Bean
    public FilterRegistrationBean buildOptionsRequestFilter()
    {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setOrder(SessionConstants.OPTIONS_REQUEST_ORDER);
        filterRegistrationBean.setFilter(new OptionsRequestFilter());
        filterRegistrationBean.setName("optionsRequestFilter" );
        filterRegistrationBean.addUrlPatterns("/*" );
        return filterRegistrationBean;
    }



}