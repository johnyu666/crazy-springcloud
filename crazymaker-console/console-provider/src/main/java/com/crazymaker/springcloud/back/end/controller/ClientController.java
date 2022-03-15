package com.crazymaker.springcloud.back.end.controller;

import com.crazymaker.springcloud.back.end.api.dto.ClientDto;
import com.crazymaker.springcloud.back.end.dao.po.Client;
import com.crazymaker.springcloud.back.end.service.impl.ClientServiceImpl;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 应用相关接口
 *
 * @author zlt
 */
@Api(tags = "应用" )
@RestController
@RequestMapping("/clients" )
public class ClientController
{
    @Autowired
    private ClientServiceImpl clientService;

    @GetMapping("/list" )
    @ApiOperation(value = "应用列表" )
    public PageOut<Client> list(@RequestParam Map<String, Object> params)
    {
        return clientService.listClent(params, true);
    }

    @GetMapping("/{id}" )
    @ApiOperation(value = "根据id获取应用" )
    public Client get(@PathVariable Long id)
    {
        return clientService.getById(id);
    }

    @GetMapping("/all" )
    @ApiOperation(value = "所有应用" )
    public RestOut<List<Client>> allClient()
    {
        PageOut<Client> page = clientService.listClent(Maps.newHashMap(), false);
        return RestOut.success(page.getData());
    }

    @DeleteMapping("/{id}" )
    @ApiOperation(value = "删除应用" )
    public void delete(@PathVariable Long id)
    {
        clientService.delClient(id);
    }

    @PostMapping("/saveOrUpdate" )
    @ApiOperation(value = "保存或者修改应用" )
    public RestOut<String> saveOrUpdate(@RequestBody ClientDto clientDto)
    {
        return clientService.saveClient(clientDto);
    }
}
