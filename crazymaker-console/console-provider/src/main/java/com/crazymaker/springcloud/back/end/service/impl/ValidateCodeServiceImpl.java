package com.crazymaker.springcloud.back.end.service.impl;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.exception.CustomAuthenticationException;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Service
public class ValidateCodeServiceImpl
{
    @Autowired
    private RedisRepository redisRepository;


    /**
     * 保存用户验证码，和randomStr绑定
     *
     * @param deviceId  客户端生成
     * @param imageCode 验证码信息
     */
    public void saveImageCode(String deviceId, String imageCode)
    {
        redisRepository.setExpire(buildKey(deviceId),
                imageCode, SessionConstants.DEFAULT_IMAGE_EXPIRE);
    }

    /**
     * 获取验证码
     *
     * @param deviceId 前端唯一标识/手机号
     */
    public String getCode(String deviceId)
    {
        return (String) redisRepository.getStr(buildKey(deviceId));
    }

    /**
     * 删除验证码
     *
     * @param deviceId 前端唯一标识/手机号
     */
    public void remove(String deviceId)
    {
        redisRepository.del(buildKey(deviceId));
    }

    /**
     * 验证验证码
     */
    public void validate(HttpServletRequest request)
    {
        String deviceId = request.getParameter("deviceId" );
        if (StringUtils.isBlank(deviceId))
        {
            throw new CustomAuthenticationException("请在请求参数中携带deviceId参数" );
        }

        String code = this.getCode(deviceId);
        String codeInRequest;
        try
        {
            codeInRequest = ServletRequestUtils.getStringParameter(request, "validCode" );
        } catch (ServletRequestBindingException e)
        {
            throw new CustomAuthenticationException("获取验证码的值失败" );
        }
        if (StringUtils.isBlank(codeInRequest))
        {
            throw new CustomAuthenticationException("请填写验证码" );
        }
        if (code == null)
        {
            throw new CustomAuthenticationException("验证码不存在或已过期" );
        }

        if (!StringUtils.equals(code, codeInRequest.toLowerCase()))
        {
            throw new CustomAuthenticationException("验证码不正确" );
        }

        this.remove(deviceId);
    }

    private String buildKey(String deviceId)
    {
        return SessionConstants.DEFAULT_CODE_KEY + ":backend:" + deviceId;
    }
}
