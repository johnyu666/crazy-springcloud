package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.constant.SeckillConstants;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillGoodDTO;
import com.crazymaker.springcloud.seckill.dao.po.SeckillGoodPO;
import com.crazymaker.springcloud.seckill.service.impl.RedisSeckillServiceImpl;
import com.crazymaker.springcloud.seckill.service.impl.SeckillGoodServiceImpl;
import com.crazymaker.springcloud.standard.ratelimit.RedisRateLimitImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/seckill/good/")
@Api(tags = "秒杀练习 商品管理")
public class SeckillGoodController
{
    @Resource
    SeckillGoodServiceImpl seckillService;

    @Resource(name = "redisRateLimitImpl")
    RedisRateLimitImpl rateLimitService;

    /**
     * 秒杀服务实现 Bean
     */
    @Resource
    RedisSeckillServiceImpl redisSeckillServiceImpl;

    /**
     * 开启商品秒杀
     *
     * @param dto 商品id
     * @return 商品 goodDTO
     */
    @PostMapping("/expose/v1")
    @ApiOperation(value = "开启商品秒杀")
    RestOut<SeckillGoodDTO> expose(@RequestBody SeckillDTO dto)
    {
        Long goodId = dto.getSeckillGoodId();

        SeckillGoodDTO goodDTO = seckillService.findGoodByID(goodId);

        if (null != goodDTO)
        {
            //初始化秒杀的限流器
            rateLimitService.initLimitKey(
                    "seckill",
                    String.valueOf(goodId),
                    SeckillConstants.MAX_ENTER,
                    SeckillConstants.PER_SECKOND_ENTER

            );
            /**
             * 缓存限流 lua 的sha1 编码，方便在其他地方获取
             */
            rateLimitService.cacheSha1();
            /**
             * 缓存秒杀 lua 的sha1 编码，方便在其他地方获取
             */
            redisSeckillServiceImpl.cacheSha1();
            return RestOut.success(goodDTO).setRespMsg("秒杀开启成功");
        }

        return RestOut.error("秒杀开启失败");
    }


    /**
     * 查询商品信息
     *
     * @param dto 商品id
     * @return 商品 goodDTO
     */
    @PostMapping("/detail/v1")
    @ApiOperation(value = "查看商品信息")
    RestOut<SeckillGoodDTO> goodDetail(
            @RequestBody SeckillDTO dto)
    {
        Long goodId = dto.getSeckillGoodId();

        SeckillGoodDTO goodDTO = seckillService.findGoodByID(goodId);

        if (null != goodDTO)
        {
            //初始化秒杀的限流器
            rateLimitService.initLimitKey(
                    "seckill",
                    String.valueOf(goodId),
                    SeckillConstants.MAX_ENTER,
                    SeckillConstants.PER_SECKOND_ENTER

            );

            /**
             * 缓存lua 的sha1 编码，方便在其他地方获取
             */
            rateLimitService.cacheSha1();

            redisSeckillServiceImpl.cacheSha1();

            return RestOut.success(goodDTO).setRespMsg("查找成功");
        }


        return RestOut.error("未找到指定秒杀商品");
    }


    /**
     * 设置秒杀库存
     *
     * @param dto 商品与库存
     * @return 商品 goodDTO
     */
    @PutMapping("/stock/v1")
    @ApiOperation(value = "查看商品信息")
    RestOut<SeckillGoodDTO> setStock(
            @RequestBody SeckillDTO dto)
    {
        Long goodId = dto.getSeckillGoodId();
        Long stock = dto.getNewStockNum();

        SeckillGoodDTO goodDTO = seckillService.setNewStock(goodId, stock);

        if (null != goodDTO)
        {
            return RestOut.success(goodDTO).setRespMsg("查找成功");
        }
        return RestOut.error("未找到指定秒杀商品");
    }


    /**
     * 获取所有的秒杀商品列表
     *
     * @param pageReq 当前页 ，从1 开始,和 页的元素个数
     * @return
     */
    @PostMapping("/list/v1")
    @ApiOperation(value = "获取所有的秒杀商品列表")
    RestOut<PageOut<SeckillGoodDTO>> findAll(@RequestBody PageReq pageReq)
    {
        PageOut<SeckillGoodDTO> page = seckillService.findAll(pageReq);
        RestOut<PageOut<SeckillGoodDTO>> r = RestOut.success(page);
        return r;

    }


    /**
     * 增加秒杀的商品
     *
     * @param stockCount 库存
     * @param title      标题
     * @param price      商品原价格
     * @param costPrice  价格
     * @return
     */
    @GetMapping("/add/v1")
    @ApiOperation(value = "增加秒杀的商品")
    RestOut<SeckillGoodDTO> executeSeckill(
            @RequestParam(value = "stockCount", required = true) long stockCount,
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "price", required = true) BigDecimal price,
            @RequestParam(value = "costPrice", required = true) BigDecimal costPrice)
    {
        SeckillGoodDTO dto = seckillService.addSeckillGood(stockCount, title, price, costPrice);
        return RestOut.success(dto).setRespMsg("暴露成功");

    }

    /**
     * 保存秒杀到缓存
     */
    @GetMapping("/cache/v1")
    @ApiOperation(value = "保存秒杀到缓存")
    public RestOut<Integer> loadSeckillToCache()
    {
        List<SeckillGoodPO> list = seckillService.loadSeckillToCache();

        if (null != list)
        {
            return RestOut.success(list.size()).setRespMsg("数据缓存成功");
        }
        return RestOut.error("数据缓存失败");

    }


}
