package com.crazymaker.springcloud.back.end.controller;

import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
import com.crazymaker.springcloud.back.end.service.impl.SysRoleServiceImpl;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@Api(tags = "角色模块api" )
public class SysRoleController
{
    @Autowired
    private SysRoleServiceImpl sysRoleService;

    /**
     * 控制台管理查询角色
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "控制台管理查询角色" )
    @GetMapping("/roles" )
    public PageOut<SysRolePO> findRoles(@RequestParam Map<String, Object> params)
    {

        if (params.get("searchKey" ) != null)
        {
            params.put(String.valueOf(params.get("searchKey" )), params.get("searchValue" ));
        }
        return sysRoleService.findRoles(params);
    }

    /**
     * 角色新增或者更新
     *
     * @param sysRolePO
     * @return
     */
    @PostMapping("/roles/saveOrUpdate" )
    public RestOut<String> saveOrUpdate(@RequestBody SysRolePO sysRolePO)
    {
        return sysRoleService.saveOrUpdateRole(sysRolePO);
    }

    /**
     * 控制台管理删除角色
     * delete /role/1
     *
     * @param id
     */
    @ApiOperation(value = "控制台管理删除角色" )
    @DeleteMapping("/roles/{id}" )
    public RestOut<String> deleteRole(@PathVariable Long id)
    {
        try
        {
            if (id == 1L)
            {
                return RestOut.failed("管理员不可以删除" );
            }
            sysRoleService.deleteRole(id);
            return RestOut.succeed("操作成功" );
        } catch (Exception e)
        {
            log.error("role-deleteRole-error", e);
            return RestOut.failed("操作失败" );
        }
    }
}
