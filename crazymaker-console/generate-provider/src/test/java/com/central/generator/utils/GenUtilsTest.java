package com.central.generator.utils;

import com.crazymaker.springcloud.back.generate.service.impl.SysGeneratorServiceImpl;
import com.crazymaker.springcloud.back.generate.start.GeneratorCloudApplication;
import com.crazymaker.springcloud.back.generate.utils.GenUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * GenUtils测试用例
 *
 * @author zlt
 * @date 2019/5/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GeneratorCloudApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenUtilsTest
{
    @Test
    public void testTableToJava()
    {
        String javaName = GenUtils.tableToJava("mq_topic", "mq" );
        Assertions.assertThat(javaName).isEqualTo("Topic" );
    }

    @Autowired
    private SysGeneratorServiceImpl sysGeneratorService;

    @Test
    public void testGen()
    {

        //查询表信息
        Map<String, String> table = sysGeneratorService.queryTable("mq_topic" );
        //查询列信息
        List<Map<String, String>> columns = sysGeneratorService.queryColumns("mq_topic" );
        GenUtils.generatorCode(table, columns, "f:/test/" );
    }
}
