package com.crazymaker.springcloud.base.config;

import com.crazymaker.springcloud.base.core.CustomedSessionIdResolver;
import com.crazymaker.springcloud.base.dao.SysUserDao;
import com.crazymaker.springcloud.base.dao.UserDao;
import com.crazymaker.springcloud.base.filter.CustomedSessionRepositoryFilter;
import com.crazymaker.springcloud.base.filter.SessionDataLoadFilter;
import com.crazymaker.springcloud.base.filter.SessionIdFilter;
import com.crazymaker.springcloud.base.service.impl.UserLoadService;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

@Configuration
public class RedisSessionFilterConfig
{

    public static final int START = 1000;


    @Bean
    public FilterRegistrationBean buildSessionIdFilter(
            RedisRepository redisRepository,
            RedisOperationsSessionRepository sessionRepository)
    {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setOrder(SessionConstants.FILTER_ORDER_BUILD_SESSION_ID + START);
        filterRegistrationBean.setFilter(new SessionIdFilter(redisRepository, sessionRepository));
        filterRegistrationBean.setName("sessionIdFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    //    @ConditionalOnBean(UserDao.class)
//    @DependsOn({"userDao", "sysUserDao"})
    @Bean
    UserLoadService userLoadService(UserDao userDao,
                                    SysUserDao sysUserDao)
    {
        return new UserLoadService(userDao, sysUserDao);
    }

    @DependsOn({"httpSessionIdResolver", "sessionRepository"})
    @Bean
    public FilterRegistrationBean buildSessionRepositoryFilter(CustomedSessionIdResolver httpSessionIdResolver,
                                                               RedisOperationsSessionRepository sessionRepository)
    {


        CustomedSessionRepositoryFilter sessionRepositoryFilter =
                new CustomedSessionRepositoryFilter<>(sessionRepository);
        sessionRepositoryFilter.setHttpSessionIdResolver(httpSessionIdResolver);


        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setOrder(SessionConstants.FILTER_ORDER_SESSION_REPOSITORY + START);
        filterRegistrationBean.setFilter(sessionRepositoryFilter);
        filterRegistrationBean.setName("sessionRepositoryFilter");
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

    //    @ConditionalOnBean(UserLoadService.class)
//    @DependsOn({"userLoadService", "redisRepository"})
    @Bean
    public FilterRegistrationBean buildSessionDataLoadFilter(
            UserLoadService userLoadService, RedisRepository redisRepository)
    {
        FilterRegistrationBean filterRegistrationBean =
                new FilterRegistrationBean();
        filterRegistrationBean.setOrder(SessionConstants.FILTER_ORDER_SESSION_DATA_LOAD + START);
        SessionDataLoadFilter filter =
                new SessionDataLoadFilter(userLoadService, redisRepository);
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setName("sessionDataLoadFilter");
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }


}