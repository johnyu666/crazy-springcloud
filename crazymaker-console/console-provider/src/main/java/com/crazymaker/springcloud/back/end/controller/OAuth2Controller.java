package com.crazymaker.springcloud.back.end.controller;

import com.crazymaker.springcloud.back.end.api.dto.LoginInfoDTO;
import com.crazymaker.springcloud.back.end.service.impl.BackEndSessionImpl;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2相关操作
 *
 * @author zlt
 */
@Api(tags = "OAuth2相关操作")
@Slf4j
@RestController
public class OAuth2Controller
{
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private BackEndSessionImpl backEndSessionImpl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @ApiOperation(value = "用户名密码获取token")
    @PostMapping("/oauth/user/token")
    public void getUserTokenInfo(
            String username,
            String password,
            String deviceId,
            String validCode,
            HttpServletRequest request, HttpServletResponse response)
    {
        LoginInfoDTO authentication = new LoginInfoDTO();

        authentication.setUsername(username);
        authentication.setPassword(password);

        try
        {
            OAuth2AccessToken oAuth2AccessToken = backEndSessionImpl.login(authentication);
//            authentication.setAuthenticated(true);

            ResponseUtil.responseSucceed(objectMapper, response, oAuth2AccessToken);
        } catch (Exception e)
        {

            throw BusinessException.builder().errMsg(e.getMessage()).build();
        }
    }

    private void exceptionHandler(HttpServletResponse response, Exception e) throws IOException
    {
        log.error("exceptionHandler-error:", e);
        exceptionHandler(response, e.getMessage());
    }

    private void exceptionHandler(HttpServletResponse response, String msg) throws IOException
    {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ResponseUtil.responseFailed(objectMapper, response, msg);
    }

    private ClientDetails getClient(String clientId, String clientSecret)
    {
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        if (clientDetails == null)
        {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
        } else if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret()))
        {
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
        }
        return clientDetails;
    }
}
