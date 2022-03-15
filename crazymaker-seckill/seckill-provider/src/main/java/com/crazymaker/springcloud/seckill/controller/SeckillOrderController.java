package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.service.impl.SeckillOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/seckill/order/")
@Api(tags = "秒杀练习 订单管理")
public class SeckillOrderController
{
    @Resource
    SeckillOrderServiceImpl seckillOrderService;


    /**
     * 查询用户订单信息
     *
     * @param userId 用户id
     * @return 商品 dto
     */
    @PostMapping("/user/{id}/list/v1")
    @ApiOperation(value = "查询用户订单信息")
    RestOut<PageOut<SeckillOrderDTO>> userOrders(
            @PathVariable(value = "id") Long userId, @RequestBody PageReq pageReq)
    {
        PageOut<SeckillOrderDTO> dto = seckillOrderService.findOrderByUserID(userId, pageReq);
        if (null != dto)
        {
            return RestOut.success(dto).setRespMsg("查询成功");
        }
        return RestOut.error("查询失败");
    }


    /**
     * 清除用户订单信息
     *
     * @param dto 含有  用户id的dto
     * @return 操作结果
     */
    @PostMapping("/user/clear/v1")
    @ApiOperation(value = "清除用户订单信息")
    RestOut<String> userOrdersClear(@RequestBody SeckillDTO dto)
    {
        Long userId = dto.getUserId();
        String result = seckillOrderService.clearOrderByUserID(userId);

        return RestOut.success(result).setRespMsg("处理完成");
    }


}
