package com.crazymaker.springcloud.seckill.dao;

import com.crazymaker.springcloud.seckill.dao.po.SeckillGoodPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface SeckillGoodDao extends
        JpaRepository<SeckillGoodPO, Long>, JpaSpecificationExecutor<SeckillGoodPO>
{

    @Transactional
    @Modifying
    @Query("update SeckillGoodPO  g set g.stockCount = g.stockCount-1  where g.id = :id" )
    int updateStockCountById(@Param("id" ) Long id);
}
