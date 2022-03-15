package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.LinkedList;

/**
 * swagger配置
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig
{

    @Bean
    public Docket templateApi()
    {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name(SessionConstants.AUTHORIZATION_HEAD).description("用户端 token 令牌" )
                .modelRef(new ModelRef("string" )).parameterType("header" ).required(false).build();
        ParameterBuilder adminTokenPar = new ParameterBuilder();
        adminTokenPar.name(SessionConstants.ADMIN_AUTHORIZATION_HEAD).description("管理控制台 token 令牌" )
                .modelRef(new ModelRef("string" )).parameterType("header" ).required(false).build();
        ParameterBuilder user = new ParameterBuilder();
        user.name(SessionConstants.USER_IDENTIFIER).description("session seed：user-id" )
                .modelRef(new ModelRef("string" )).parameterType("header" ).required(false).build();
        LinkedList<Parameter> list = new LinkedList<>();
        list.add(tokenPar.build());
        list.add(adminTokenPar.build());
        list.add(user.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build().globalOperationParameters(list);
    }


    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("疯狂创客圈 springcloud + Nginx 高并发核心编程" )
                .description("Zuul+Swagger2  构建  RESTful APIs" )
                .termsOfServiceUrl("https://www.cnblogs.com/crazymakercircle/" )
                .contact(new Contact("疯狂创客圈", "https://www.cnblogs.com/crazymakercircle/", "" ))
                .version("1.0" )
                .build();
    }
}
