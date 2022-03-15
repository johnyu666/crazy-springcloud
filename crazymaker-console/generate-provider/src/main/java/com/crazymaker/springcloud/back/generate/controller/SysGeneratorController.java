package com.crazymaker.springcloud.back.generate.controller;

import com.crazymaker.springcloud.back.generate.service.impl.SysGeneratorServiceImpl;
import com.crazymaker.springcloud.common.page.PageOut;
import io.swagger.annotations.Api;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: zlt
 */
@RestController
@Api(tags = "代码生成器" )
@RequestMapping("/generator" )
public class SysGeneratorController
{

    @Autowired
    private SysGeneratorServiceImpl sysGeneratorService;

    /**
     * 列表
     */
    @ResponseBody
    @GetMapping("/list" )
    public PageOut getTableList(@RequestParam Map<String, Object> params)
    {
//        if (params.get("searchKey") != null) {
//            params.put(String.valueOf(params.get("searchKey")), params.get("searchValue"));
//        }
        return sysGeneratorService.queryList(params);
    }

    /**
     * 生成代码FileUtil
     */
    @GetMapping("/code" )
    public void makeCode(String tables, HttpServletResponse response) throws IOException
    {
        byte[] data = sysGeneratorService.generatorCode(tables.split("," ));
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"generator.zip\"" );
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8" );
        IOUtils.write(data, response.getOutputStream());
    }
}
