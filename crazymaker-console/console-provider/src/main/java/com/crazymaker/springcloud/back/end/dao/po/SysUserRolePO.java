package com.crazymaker.springcloud.back.end.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "SYS_USER_ROLE" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserRolePO implements Serializable
{

    //ID
    @Id
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator" )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator" )
    @Column(name = "ID", unique = true, nullable = false, length = 8)
    private Long id;


    private static final long serialVersionUID = 8897443010220586111L;
    @Column(name = "USER_ID" )
    private Long userId;
    @Column(name = "ROLE_ID" )
    private Long roleId;


}
