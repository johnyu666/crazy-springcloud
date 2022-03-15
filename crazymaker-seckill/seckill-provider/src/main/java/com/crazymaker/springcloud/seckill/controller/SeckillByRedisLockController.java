package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.distribute.rateLimit.RateLimitService;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.constant.SeckillConstants;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillGoodDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.service.impl.RedisSeckillServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/seckill/redis/")
@Api(tags = "秒杀练习  RedisLock 版本")
public class SeckillByRedisLockController
{
    /**
     * 秒杀服务实现 Bean
     */
    @Resource
    RedisSeckillServiceImpl redisSeckillServiceImpl;


    /**
     * 获取秒杀的令牌
     */
    @ApiOperation(value = "获取秒杀的令牌")
    @PostMapping("/token/v1")
    RestOut<String> getSeckillToken(
            @RequestBody SeckillDTO dto)
    {

        String result = redisSeckillServiceImpl.getSeckillToken(
                dto.getSeckillGoodId(),
                dto.getUserId());
        return RestOut.success(result).setRespMsg("这是获取的结果");

    }

    /**
     * 执行秒杀的操作
     *
     * @return
     */
    @ApiOperation(value = "秒杀")
    @PostMapping("/do/v1")
    RestOut<SeckillOrderDTO> executeSeckill(@RequestBody SeckillDTO dto)
    {
        SeckillOrderDTO orderDTO = redisSeckillServiceImpl.executeSeckill(dto);
        return RestOut.success(orderDTO).setRespMsg("秒杀成功");
    }



}
