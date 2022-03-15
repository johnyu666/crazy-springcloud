package com.crazymaker.springcloud.message.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "application.message.queue" )
public class MqConfigProperties
{
    private String consumer;
    private String host;
    private String port;
    private String user;
    private String password;
}

