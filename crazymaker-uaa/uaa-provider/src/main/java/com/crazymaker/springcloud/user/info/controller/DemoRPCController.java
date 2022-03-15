package com.crazymaker.springcloud.user.info.controller;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.remote.client.DemoClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/call/demo/")
@Api(tags = "演示 demo-provider 远程调用")
public class DemoRPCController
{

    //注入 @FeignClient 注解配置 所配置的 demo-provider 客户端Feign实例
    @Resource
    DemoClient demoClient;

    @GetMapping("/hello/v1")
    @ApiOperation(value = "hello 远程调用")
    public RestOut<JSONObject> remoteHello()
    {
        /**
         * 调用   demo-provider 的  REST 接口  api/demo/hello/v1
         */
        RestOut<JSONObject> result = demoClient.hello();
        JSONObject data = new JSONObject();
        data.put("demo-data", result);
        return RestOut.success(data).setRespMsg("操作成功");
    }


    @GetMapping("/echo/{word}/v1")
    @ApiOperation(value = "echo 远程调用")
    public RestOut<JSONObject> remoteEcho(
            @PathVariable(value = "word") String word)
    {
        /**
         * 调用  demo-provider 的  REST 接口   api/demo/echo/{0}/v1
         */
        RestOut<JSONObject> result = demoClient.echo(word);
        JSONObject data = new JSONObject();
        data.put("demo-data", result);
        return RestOut.success(data).setRespMsg("操作成功");
    }
}
