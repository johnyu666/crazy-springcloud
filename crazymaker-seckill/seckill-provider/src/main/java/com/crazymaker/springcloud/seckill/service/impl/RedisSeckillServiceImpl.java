package com.crazymaker.springcloud.seckill.service.impl;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.IOUtil;
import com.crazymaker.springcloud.seckill.api.constant.SeckillConstants;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillGoodDao;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillGoodPO;
import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@Service
public class RedisSeckillServiceImpl
{

    /**
     * 秒杀商品的 DAO 数据操作类
     */
    @Resource
    SeckillGoodDao seckillGoodDao;
    /**
     * 秒杀订单的 DAO 数据操作类
     */
    @Resource
    SeckillOrderDao seckillOrderDao;


    /**
     * redis 分布式锁实现类
     */
    @Autowired
    RedisLockService redisLockService;

    /**
     * 缓存数据操作类
     */
    @Resource
    RedisRepository redisRepository;


    /**
     * 秒杀令牌操作的脚本
     */
    static String seckillLua = "script/seckill.lua";
    static RedisScript<Long> seckillScript = null;

    {
        String script = IOUtil.loadJarFile(RedisLockService.class.getClassLoader(), seckillLua);
        seckillScript = new DefaultRedisScript<>(script, Long.class);
    }


    /**
     * 获取秒杀令牌
     *
     * @param seckillGoodId 秒杀id
     * @param userId        用户id
     * @return 令牌信息
     */
    public String getSeckillToken(Long seckillGoodId, Long userId)
    {


        String token = UUID.randomUUID().toString();
        Long res = redisRepository.executeScript(
                seckillScript, Collections.singletonList("setToken"),
                String.valueOf(seckillGoodId),
                String.valueOf(userId),
                token
        );

        if (res == 2)
        {
            throw BusinessException.builder().errMsg("秒杀商品没有找到").build();
        }

        if (res == 4)
        {
            throw BusinessException.builder().errMsg("库存不足,稍后再来").build();
        }

        if (res == 5)
        {
            throw BusinessException.builder().errMsg("已经排队过了").build();
        }


        if (res != 1)
        {
            throw BusinessException.builder().errMsg("排队失败,未知错误").build();

        }
        return token;
    }


    /**
     * 执行秒杀下单
     *
     * @param inDto
     * @return
     */
    public SeckillOrderDTO executeSeckill(SeckillDTO inDto)
    {
        long goodId = inDto.getSeckillGoodId();
        Long userId = inDto.getUserId();

        Long res = redisRepository.executeScript(
                seckillScript, Collections.singletonList("checkToken"),
                String.valueOf(inDto.getSeckillGoodId()),
                String.valueOf(inDto.getUserId()),
                inDto.getSeckillToken()
        );

        if (res != 5)
        {
            throw BusinessException.builder().errMsg("请提前排队").build();
        }


        /**
         * 创建订单对象
         */
        SeckillOrderPO order =
                SeckillOrderPO.builder()
                        .goodId(goodId).userId(userId).build();


        Date nowTime = new Date();
        order.setCreateTime(nowTime);
        order.setStatus(SeckillConstants.ORDER_VALID);


        String lockValue = UUID.randomUUID().toString();
        SeckillOrderDTO dto = null;

        /**
         * 创建重复性检查的订单对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().goodId(
                        order.getGoodId()).userId(order.getUserId()).build();

        //记录秒杀订单信息
        long insertCount = seckillOrderDao.count(Example.of(checkOrder));

        //唯一性判断：goodId,id 保证一个用户只能秒杀一件商品
        if (insertCount >= 1)
        {
            //重复秒杀
            log.error("重复秒杀");
            throw BusinessException.builder().errMsg("重复秒杀").build();
        }

        /**
         * 获取分布式锁
         */
        String lockKey = "seckill:lock:" + String.valueOf(goodId);
        boolean locked = redisLockService.acquire(lockKey, lockValue, 1, TimeUnit.SECONDS);
        /**
         * 执行秒杀，秒杀前先抢到分布式锁
         */
        if (locked)
        {

            Optional<SeckillGoodPO> optional = seckillGoodDao.findById(order.getGoodId());
            if (!optional.isPresent())
            {
                //秒杀不存在
                throw BusinessException.builder().errMsg("秒杀不存在").build();
            }


            //查询库存
            SeckillGoodPO good = optional.get();
            if (good.getStockCount() <= 0)
            {
                //重复秒杀
                throw BusinessException.builder().errMsg("秒杀商品被抢光").build();
            }

            order.setMoney(good.getCostPrice());
            try
            {

                /**
                 * 进入秒杀事务
                 * 执行秒杀逻辑：1.减库存；2.储存秒杀订单
                 */
                doSeckill(order);
                dto = new SeckillOrderDTO();
                BeanUtils.copyProperties(order, dto);
            } finally
            {
                try
                {
                    /**
                     * 释放分布式锁
                     */
                    redisLockService.release(lockKey, lockValue);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        } else
        {
            throw BusinessException.builder().errMsg("获取分布式锁失败").build();
        }

        return dto;

    }


    /**
     * 秒杀事务
     *
     * @param order 订单
     */
    @Transactional
    public void doSeckill(SeckillOrderPO order)
    {


        /**
         * 插入秒杀订单
         */
        seckillOrderDao.save(order);

        //减库存

        seckillGoodDao.updateStockCountById(order.getGoodId());
    }

    /**
     * 获取 redis lua 脚本的 sha1 编码,并缓存到 redis
     */
    public String cacheSha1()
    {
        String sha1 = seckillScript.getSha1();
        redisRepository.set("lua:sha1:seckill", sha1);
        return sha1;
    }
}
