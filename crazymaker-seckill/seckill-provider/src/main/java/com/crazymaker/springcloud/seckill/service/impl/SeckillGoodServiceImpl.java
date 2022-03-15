package com.crazymaker.springcloud.seckill.service.impl;

import com.crazymaker.springcloud.common.constants.ConfigConstants;
import com.crazymaker.springcloud.common.distribute.rateLimit.RateLimitService;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.page.DataAdapter;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.util.Encrypt;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.seckill.api.dto.SeckillGoodDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillGoodDao;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillGoodPO;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
@Service
public class SeckillGoodServiceImpl
{

    @Resource
    SeckillGoodDao seckillGoodDao;

    @Resource
    SeckillOrderDao seckillOrderDao;

    @Resource(name = "zkRateLimitServiceImpl" )
    RateLimitService zkRateLimitServiceImpl;

    @Resource
    RedisRepository redisRepository;

    @Autowired
    RedisLockService redisLockService;


    public SeckillGoodDTO findGoodByID(Long id)
    {

        Optional<SeckillGoodPO> optional = seckillGoodDao.findById(id);

        if (optional.isPresent())
        {
            SeckillGoodDTO dto = new SeckillGoodDTO();
            SeckillGoodPO good = optional.get();
            redisRepository.set(String.valueOf(good.getId()), JsonUtil.pojoToJson(good));
            cacheGood(good);
            BeanUtils.copyProperties(optional.get(), dto);
            return dto;
        }
        return null;

    }

    private void cacheGood(SeckillGoodPO good)
    {
        if (null == good)
        {
            return;
        }
        redisRepository.set(
                ConfigConstants.CONFIG_SECKILL_GOODS + String.valueOf(good.getId()), JsonUtil.pojoToJson(good));
    }


    /**
     * 获取所有的秒杀商品列表
     *
     * @param pageReq 当前页 ，从1 开始,和 页的元素个数
     * @return
     */
    public PageOut<SeckillGoodDTO> findAll(PageReq pageReq)
    {
        Specification<SeckillGoodPO> specification = getSeckillGoodPOSpecification();

        Page<SeckillGoodPO> page = seckillGoodDao.findAll(specification, PageRequest.of(pageReq.getJpaPage(), pageReq.getPageSize()));

        PageOut<SeckillGoodDTO> pageData = DataAdapter.adapterPage(page, SeckillGoodDTO.class);

        return pageData;

    }


    /**
     * 秒杀暴露
     *
     * @param gooId 商品id
     * @return 暴露的秒杀商品
     */
    public SeckillGoodDTO exposeSeckillGood(long gooId)
    {
        Optional<SeckillGoodPO> optional = seckillGoodDao.findById(gooId);
        if (!optional.isPresent())
        {
            //秒杀不存在
            throw BusinessException.builder().errMsg("秒杀不存在" ).build();
        }
        SeckillGoodPO goodPO = optional.get();

        Date startTime = goodPO.getStartTime();
        Date endTime = goodPO.getEndTime();
        //获取系统时间
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime())
        {
            //秒杀不存在
            throw BusinessException.builder().errMsg("秒杀没有开始" ).build();
        }

        if (nowTime.getTime() > endTime.getTime())
        {
            //秒杀已经结束
            throw BusinessException.builder().errMsg("秒杀已经结束" ).build();
        }
        //转换特定字符串的过程，不可逆的算法
        String md5 = Encrypt.getMD5(String.valueOf(gooId));

        SeckillGoodDTO dto = new SeckillGoodDTO();
        BeanUtils.copyProperties(goodPO, dto);
        dto.setMd5(md5);
        dto.setExposed(true);
        return dto;
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
    public SeckillGoodDTO addSeckillGood(long stockCount, String title, BigDecimal price, BigDecimal costPrice)
    {
        //获取系统时间
        Date nowTime = new Date();


        SeckillGoodPO po = new SeckillGoodPO();
        po.setCostPrice(costPrice);
        po.setPrice(price);
        po.setTitle(title);
        po.setStockCount(stockCount);

        po.setCreateTime(nowTime);
        po.setStartTime(DateUtils.addMonths(nowTime, -1));
        po.setEndTime(DateUtils.addMonths(nowTime, 1));

        seckillGoodDao.save(po);
        SeckillGoodDTO dto = new SeckillGoodDTO();
        BeanUtils.copyProperties(po, dto);

        return dto;
    }


    /**
     * 保存秒杀到缓存
     */
    public List<SeckillGoodPO> loadSeckillToCache()
    {
        Specification<SeckillGoodPO> specification = getSeckillGoodPOSpecification();
        List<SeckillGoodPO> list = seckillGoodDao.findAll(specification);

        if (null == list || list.size() < 1)
        {
            return null;
        }

        list.stream().forEach(good ->
        {
            cacheGood(good);
        });
//        Map<String, String> stringStringMap = new LinkedHashMap<>();

//        String key = ConfigConstants.CONFIG_SECKILL_GOODS;
//
//        getRedisRepository().del(key);
//        getRedisRepository().hPutAll(key, stringStringMap);

        return list;
    }

    private Specification<SeckillGoodPO> getSeckillGoodPOSpecification()
    {
        //获取系统时间
        Date nowTime = new Date();
        return new Specification<SeckillGoodPO>()
        {
            @Override
            public Predicate toPredicate(Root<SeckillGoodPO> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb)
            {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.greaterThanOrEqualTo(root.get("endTime" ), nowTime));
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime" ), nowTime));
                predicates.add(cb.greaterThan(root.get("stockCount" ), 0));

                // and到一起的话所有条件就是且关系，or就是或关系
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public SeckillGoodDTO setNewStock(Long goodId, Long stock)
    {
        Optional<SeckillGoodPO> optional = seckillGoodDao.findById(goodId);

        if (optional.isPresent())
        {
            SeckillGoodPO po = optional.get();
            po.setStockCount(stock);
            po.setRawStockCount(stock);
            seckillGoodDao.save(po);
            SeckillGoodDTO dto = new SeckillGoodDTO();
            cacheGood(po);
            BeanUtils.copyProperties(po, dto);
            return dto;
        }
        return null;
    }
}
