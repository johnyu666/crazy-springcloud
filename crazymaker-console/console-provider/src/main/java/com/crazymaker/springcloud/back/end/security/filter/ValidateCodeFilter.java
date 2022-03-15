package com.crazymaker.springcloud.back.end.security.filter;

import com.crazymaker.springcloud.back.end.service.impl.ValidateCodeServiceImpl;
import com.crazymaker.springcloud.common.exception.CustomAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zlt
 * @date 2018/11/21
 */
@Slf4j
@Component("validateCodeFilter" )
public class ValidateCodeFilter extends OncePerRequestFilter
{


    @Autowired
    private ValidateCodeServiceImpl validateCodeService;


    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        if (request.getMethod().equals("OPTIONS" ))
        {
            return true;
        }

        //登录提交的时候验证验证码
        if (pathMatcher.match("/oauth/user/token", request.getRequestURI()))
        {

            return false;
        }
        return true;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try
        {
            validateCodeService.validate(request);
        } catch (CustomAuthenticationException e)
        {
            authenticationFailureHandler.onAuthenticationFailure(
                    request,
                    response,
                    e);
            return;
        }

        chain.doFilter(request, response);
    }
}