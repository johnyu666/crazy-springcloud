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


@Entity
@Table(name = "oauth_client_details" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client implements Serializable
{
    private static final long serialVersionUID = 799360940290141180L;


    //用户ID
    @Id
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator" )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator" )
    @Column(name = "id", unique = true, nullable = false, length = 8)
    private Long id;

    @Column(name = "client_id" )
    private String clientId;
    /**
     * 应用名称
     */
    @Column(name = "client_name" )
    private String clientName;
    @Column(name = "resource_ids" )
    private String resourceIds = null;
    //    private String resourceIds = "";
    @Column(name = "client_secret" )
    private String clientSecret;
    @Column(name = "client_secret_str" )
    private String clientSecretStr;

    @Column(name = "scope" )
    private String scope = null;
    //    private String scope = "all";
    @Column(name = "authorized_grant_types" )
    private String authorizedGrantTypes = null;
//    private String authorizedGrantTypes = "authorization_code,password,refresh_token,client_credentials";

    @Column(name = "web_server_redirect_uri" )
    private String webServerRedirectUri;
    @Column(name = "authorities" )
    private String authorities = null;
    @Column(name = "access_token_validity" )
    private Integer accessTokenValiditySeconds = null;
    //    private Integer accessTokenValiditySeconds = 18000;
    @Column(name = "refresh_token_validity" )
    private Integer refreshTokenValiditySeconds = null;
    //    private Integer refreshTokenValiditySeconds = 28800;
    @Column(name = "additional_information" )
    private String additionalInformation = null;
    //    private String additionalInformation = "{}";
    @Column(name = "autoapprove" )
    private String autoapprove = null;
//    private String autoapprove = "true";


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
}
