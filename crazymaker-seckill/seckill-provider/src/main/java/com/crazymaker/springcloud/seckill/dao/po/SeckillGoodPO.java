package com.crazymaker.springcloud.seckill.dao.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品PO
 * 说明： 秒杀商品表和主商品表不同
 */

@Entity
@Table(name = "SECKILL_GOOD")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeckillGoodPO implements Serializable
{
    //商品ID
    @Id

    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator")
    @Column(name = "GOOD_ID", unique = true, nullable = false, length = 8)
    private Long id;

    //商品标题
    @Column(name = "GOOD_TITLE", length = 400)
    private String title;

    //商品标题
    @Column(name = "GOOD_IMAGE", length = 400)
    private String image;

    //商品原价格
    @Column(name = "GOOD_PRICE")
    private BigDecimal price;

    //商品秒杀价格
    @Column(name = "COST_PRICE")
    private BigDecimal costPrice;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "CREATE_TIME")
    private Date createTime;

    //秒杀开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "START_TIME")
    private Date startTime;

    //秒杀结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "END_TIME")
    private Date endTime;

    //剩余库存数量
    @Column(name = "STOCK_COUNT")
    private long stockCount;
    //原始库存数量
    @Column(name = "raw_stock")
    private long rawStockCount;
}
