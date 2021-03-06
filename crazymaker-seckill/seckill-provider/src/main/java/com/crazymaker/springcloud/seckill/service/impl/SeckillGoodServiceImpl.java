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
     * ?????????????????????????????????
     *
     * @param pageReq ????????? ??????1 ??????,??? ??????????????????
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
     * ????????????
     *
     * @param gooId ??????id
     * @return ?????????????????????
     */
    public SeckillGoodDTO exposeSeckillGood(long gooId)
    {
        Optional<SeckillGoodPO> optional = seckillGoodDao.findById(gooId);
        if (!optional.isPresent())
        {
            //???????????????
            throw BusinessException.builder().errMsg("???????????????" ).build();
        }
        SeckillGoodPO goodPO = optional.get();

        Date startTime = goodPO.getStartTime();
        Date endTime = goodPO.getEndTime();
        //??????????????????
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime())
        {
            //???????????????
            throw BusinessException.builder().errMsg("??????????????????" ).build();
        }

        if (nowTime.getTime() > endTime.getTime())
        {
            //??????????????????
            throw BusinessException.builder().errMsg("??????????????????" ).build();
        }
        //???????????????????????????????????????????????????
        String md5 = Encrypt.getMD5(String.valueOf(gooId));

        SeckillGoodDTO dto = new SeckillGoodDTO();
        BeanUtils.copyProperties(goodPO, dto);
        dto.setMd5(md5);
        dto.setExposed(true);
        return dto;
    }

    /**
     * ?????????????????????
     *
     * @param stockCount ??????
     * @param title      ??????
     * @param price      ???????????????
     * @param costPrice  ??????
     * @return
     */
    public SeckillGoodDTO addSeckillGood(long stockCount, String title, BigDecimal price, BigDecimal costPrice)
    {
        //??????????????????
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
     * ?????????????????????
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
        //??????????????????
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

                // and?????????????????????????????????????????????or???????????????
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
