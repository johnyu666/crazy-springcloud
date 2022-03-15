package com.crazymaker.springcloud.back.end.dao.po;

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
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "SYS_MENU" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysMenuPO implements Serializable
{
    private static final long serialVersionUID = 749360940290141180L;


    //用户ID
    @Id
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator" )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator" )
    @Column(name = "MENU_ID", unique = true, nullable = false, length = 8)
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

    @Column(name = "PARENT_ID" )
    private Long parentId;
    @Column(name = "NAME" )
    private String name;
    @Column(name = "CSS" )
    private String css;
    @Column(name = "URL" )
    private String url;
    @Column(name = "PATH" )
    private String path;
    @Column(name = "SORT" )
    private Integer sort;
    @Column(name = "TYPE" )
    private Integer type;
    @Column(name = "HIDDEN" )
    private Boolean hidden;


    @Transient
    public Set<SysRolePO> roleSet;
    /**
     * 请求的类型
     */
    @Transient
    private String pathMethod;
    @Transient
    private List<SysMenuPO> subMenus;
    @Transient
    private Long roleId;
    @Transient
    private Set<Long> menuIds;
}
