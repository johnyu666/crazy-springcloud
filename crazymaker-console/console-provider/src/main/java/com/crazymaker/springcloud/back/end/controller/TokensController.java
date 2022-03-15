package com.crazymaker.springcloud.back.end.controller;

import com.crazymaker.springcloud.back.end.service.impl.RedisTokensServiceImpl;
import com.crazymaker.springcloud.common.dto.TokenVo;
import com.crazymaker.springcloud.common.page.PageOut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * token管理接口
 *
 * @author zlt
 */
@Api(tags = "Token管理" )
@RestController
@RequestMapping("/tokens" )
public class TokensController
{
    @Autowired
    private RedisTokensServiceImpl tokensService;

    @GetMapping("" )
    @ApiOperation(value = "token列表" )
    public PageOut<TokenVo> list(@RequestParam Map<String, Object> params, String tenantId)
    {
        return tokensService.listTokens(params, tenantId);
    }
}
