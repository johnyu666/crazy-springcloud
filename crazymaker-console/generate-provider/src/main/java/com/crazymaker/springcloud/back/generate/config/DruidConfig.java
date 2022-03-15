package com.crazymaker.springcloud.back.generate.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;


@Configuration
@Slf4j
public class DruidConfig
{

    @Value("${spring.datasource.url}" )
    private String dbUrl;

    @Value("${spring.datasource.type}" )
    private String type;


    @Value("${spring.datasource.username}" )
    private String username;

    @Value("${spring.datasource.password}" )
    private String password;

    @Value("${spring.datasource.driver-class-name}" )
    private String driverClassName;

    @Value("${spring.datasource.initialSize}" )
    private int initialSize;

    @Value("${spring.datasource.minIdle}" )
    private int minIdle;

    @Value("${spring.datasource.maxActive}" )
    private int maxActive;

    @Value("${spring.datasource.maxWait}" )
    private int maxWait;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}" )
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}" )
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.validationQuery}" )
    private String validationQuery;

    @Value("${spring.datasource.testWhileIdle}" )
    private boolean testWhileIdle;

    @Value("${spring.datasource.testOnBorrow}" )
    private boolean testOnBorrow;

    @Value("${spring.datasource.testOnReturn}" )
    private boolean testOnReturn;

    @Value("${spring.datasource.filters}" )
    private String filters;

    @Value("${spring.datasource.logSlowSql}" )
    private String logSlowSql;

    @Bean
    public ServletRegistrationBean druidServlet()
    {


        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid1/*" );
        /** 初始化参数配置，initParams**/
        //白名单
//    bean.addInitParameter("allow", "127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
//    bean.addInitParameter("deny", "192.168.1.73");
        //登录查看信息的账号密码.
        bean.addInitParameter("loginUsername", username);
        bean.addInitParameter("loginPassword", password);
        bean.addInitParameter("logSlowSql", logSlowSql);
        return bean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean()
    {
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        bean.addUrlPatterns("/*" );
        //添加不需要忽略的格式信息.
        bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/druid2/*" );
        bean.addInitParameter("profileEnable", "true" );
        return bean;

    }

    @Bean
    public DataSource druidDataSource()
    {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        //datasource.setDbType(type);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        try
        {
            datasource.setFilters(filters);
        } catch (SQLException e)
        {
            log.error("druid configuration initialization filter", e);
        }
        return datasource;
    }

}