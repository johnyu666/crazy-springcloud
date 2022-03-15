package com.crazymaker.springcloud.seckill.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 秒杀 dto
 * 说明： 秒杀商品表和主商品表不同
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeckillDTO implements Serializable
{
    //秒杀用户的用户ID
    private Long userId;


    //秒杀商品，和订单是一对多的关系
    private Long seckillGoodId;

    //验证码
    private String seckillToken;

    //秒杀库存
    private Long newStockNum;
}
