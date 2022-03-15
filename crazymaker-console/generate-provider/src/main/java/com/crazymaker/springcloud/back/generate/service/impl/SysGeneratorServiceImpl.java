package com.crazymaker.springcloud.back.generate.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crazymaker.springcloud.back.generate.mapper.SysGeneratorMapper;
import com.crazymaker.springcloud.back.generate.utils.GenUtils;
import com.crazymaker.springcloud.common.page.PageOut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @Author zlt
 */
@Slf4j
@Service
public class SysGeneratorServiceImpl extends ServiceImpl
{
    @Autowired
    private SysGeneratorMapper sysGeneratorMapper;

    public PageOut<Map<String, Object>> queryList(Map<String, Object> params)
    {
        Page<Map<String, Object>> page = new Page<>(MapUtils.getInteger(params, "page" ), MapUtils.getInteger(params, "limit" ));

        List<Map<String, Object>> list = sysGeneratorMapper.queryList(page, params);
        return PageOut.<Map<String, Object>>builder().data(list).code(0).count(page.getTotal()).build();
    }

    public Map<String, String> queryTable(String tableName)
    {
        return sysGeneratorMapper.queryTable(tableName);
    }

    public List<Map<String, String>> queryColumns(String tableName)
    {
        return sysGeneratorMapper.queryColumns(tableName);
    }

    public byte[] generatorCode(String[] tableNames)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (
                ZipOutputStream zip = new ZipOutputStream(outputStream)
        )
        {
            for (String tableName : tableNames)
            {
                //查询表信息
                Map<String, String> table = queryTable(tableName);
                //查询列信息
                List<Map<String, String>> columns = queryColumns(tableName);
                //生成代码
                GenUtils.generatorCode(table, columns, zip);
            }
        } catch (IOException e)
        {
            log.error("generatorCode-error: ", e);
        }
        return outputStream.toByteArray();
    }
}
