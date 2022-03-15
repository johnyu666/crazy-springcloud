package com.crazymaker.springcloud.back.end.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class ClientDto
{
    private static final long serialVersionUID = 1475637288060027265L;


    private Long id;

    private String clientId;
    /**
     * 应用名称
     */
    private String clientName;
    private String resourceIds = "";
    private String clientSecret;
    private String clientSecretStr;

    private String scope = "all";
    private String authorizedGrantTypes = "authorization_code,password,refresh_token,client_credentials";

    private String webServerRedirectUri;
    private String authorities = "";
    private Integer accessTokenValiditySeconds = 18000;
    private Integer refreshTokenValiditySeconds = 28800;
    private String additionalInformation = "{}";
    private String autoapprove = "true";


    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date createTime;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date updateTime;

    private List<Long> permissionIds;

    private Set<Long> serviceIds;
}
