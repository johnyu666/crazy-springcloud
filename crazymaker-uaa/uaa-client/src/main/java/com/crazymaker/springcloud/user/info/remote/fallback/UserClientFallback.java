package com.crazymaker.springcloud.user.info.remote.fallback;


import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.user.info.remote.client.UserClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 *  Feign 客户端接口的 fallback 回退处理类
 */
@Component
public class UserClientFallback implements UserClient
{
    @Override
    public RestOut<UserDTO> detail(Long id)
    {
        return RestOut.error("FailBack：user detail rest 服务调用失败" );
    }
}
