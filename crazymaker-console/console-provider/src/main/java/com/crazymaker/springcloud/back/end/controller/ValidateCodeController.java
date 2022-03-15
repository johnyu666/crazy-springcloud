package com.crazymaker.springcloud.back.end.controller;

import com.crazymaker.springcloud.back.end.service.impl.ValidateCodeServiceImpl;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@Controller

@Api(value = "生成验证码", tags = {"生成验证码"})
@RestController
@RequestMapping("/validate" )
public class ValidateCodeController
{
    @Autowired
    private ValidateCodeServiceImpl validateCodeService;

    /**
     * 创建验证码
     *
     * @throws Exception
     */
    @RequestMapping(value = "/code/{deviceId:.+}", method = RequestMethod.GET)
//    @GetMapping("/code/{deviceId:.+}")
    public void createCode(
            @ApiParam(required = true, name = "deviceId", defaultValue = "1" ) @PathVariable String deviceId,
            HttpServletResponse response) throws Exception
    {
        Assert.notNull(deviceId, "机器码不能为空" );
        // 设置请求头为输出图片类型
        CaptchaUtil.setHeader(response);
        // 三个参数分别为宽、高、位数
        GifCaptcha gifCaptcha = new GifCaptcha(100, 35, 4);
        // 设置类型：字母数字混合
        gifCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        // 保存验证码
        validateCodeService.saveImageCode(deviceId,
                gifCaptcha.text().toLowerCase());
        // 输出图片流
        gifCaptcha.out(response.getOutputStream());
    }

}
