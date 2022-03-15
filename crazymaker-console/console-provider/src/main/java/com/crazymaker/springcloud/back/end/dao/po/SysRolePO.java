package com.crazymaker.springcloud.back.end.dao.po;

import com.crazymaker.springcloud.base.dao.po.SysUserPO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "SYS_ROLE" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysRolePO implements Serializable
{
    private static final long serialVersionUID = 4497149010220586111L;

    //用户ID
    @Id
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator" )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator" )
    @Column(name = "ROLE_ID", unique = true, nullable = false, length = 8)
    private Long id;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    @Column(name = "CREATE_TIME" )
    private Date createTime;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    @Column(name = "UPDATE_TIME" )
    private Date updateTime;


    @Column(name = "CODE" )
    private String code;

    @Column(name = "NAME" )
    private String name;


    @Transient
    public Set<SysUserPO> userSet;


    @Transient
    private Set<SysMenuPO> menuSet = new HashSet<>();

}
