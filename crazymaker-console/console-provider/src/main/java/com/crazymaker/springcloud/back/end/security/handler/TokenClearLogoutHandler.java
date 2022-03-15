package com.crazymaker.springcloud.back.end.security.handler;

import com.crazymaker.springcloud.back.end.service.impl.BackEndSessionImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenClearLogoutHandler implements LogoutHandler
{

    private BackEndSessionImpl userAuthService;

    public TokenClearLogoutHandler(BackEndSessionImpl userAuthService)
    {
        this.userAuthService = userAuthService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    {
        clearToken(authentication);
    }

    protected void clearToken(Authentication authentication)
    {
        if (authentication == null)
            return;
        UserDetails user = (UserDetails) authentication.getPrincipal();
        if (user != null && user.getUsername() != null)
        {
//            userLoginService.deleteUserLoginInfo(user.getUsername());
        }
    }

}
