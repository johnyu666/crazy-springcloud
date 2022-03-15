package com.crazymaker.springcloud.back.end.dao.po;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zlt
 * @date 2019/7/30
 */


@Entity
@Table(name = "SYS_ROLE_MENU" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysRoleMenuPO implements Serializable
{
    private static final long serialVersionUID = 8897149010220586111L;


    //ID
    @Id
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator" )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator" )
    @Column(name = "ID", unique = true, nullable = false, length = 8)
    private Long id;

    @Column(name = "MENU_ID" )
    private Long menuId;
    @Column(name = "ROLE_ID" )
    private Long roleId;


}
