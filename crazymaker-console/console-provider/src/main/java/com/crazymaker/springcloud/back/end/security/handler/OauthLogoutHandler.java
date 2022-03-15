package com.crazymaker.springcloud.back.end.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @date 2018/10/17
 */
@Slf4j
@Component("oauthLogoutHandler" )
public class OauthLogoutHandler implements LogoutHandler
{


    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication)
    {
        //todo
    }
}
