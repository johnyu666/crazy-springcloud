package com.crazymaker.springcloud.back.end.controller;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.back.end.annotation.LoginUser;
import com.crazymaker.springcloud.back.end.api.dto.SysUserDTO;
import com.crazymaker.springcloud.back.end.dao.po.LoginAppUser;
import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
import com.crazymaker.springcloud.back.end.dto.SearchDto;
import com.crazymaker.springcloud.back.end.service.impl.SysUserServiceImpl;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.context.SessionHolder;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@RestController
@Api(tags = "用户模块api" )
public class SysUserController
{
    private static final String ADMIN_CHANGE_MSG = "超级管理员不给予修改";


    @Autowired
    private SysUserServiceImpl appUserService;


    /**
     * 当前登录用户 LoginAppUser
     *
     * @return
     */
    @ApiOperation(value = "根据access_token当前登录用户" )
    @GetMapping("/users/current" )
    public RestOut<LoginAppUser> getLoginAppUser(@LoginUser(isFull = true) SysUserDTO user)
    {
        UserDTO dto = SessionHolder.getSessionUser();
        LoginAppUser loginUser = appUserService.getLoginAppUser(dto);
        SessionHolder.put(SessionConstants.SYS_ROLES_JSON, JsonUtil.pojoToJson(loginUser.getRoles()));
        return RestOut.success(loginUser, "查询成功" );
    }

    /**
     * 查询用户实体对象SysUser
     */
    @GetMapping(value = "/users/name/{username}" )
    @ApiOperation(value = "根据用户名查询用户实体" )
    @Cacheable(value = "user", key = "#username" )
    public SysUserDTO selectByUsername(@PathVariable String username)
    {
        return appUserService.findByUsername(username);
    }

    /**
     * 查询用户登录对象LoginAppUser
     */
    @GetMapping(value = "/users-anon/login", params = "username" )
    @ApiOperation(value = "根据用户名查询用户" )
    public LoginAppUser findByUsername(String username)
    {
        return appUserService.findByUsername(username);
    }

    /**
     * 通过手机号查询用户、角色信息
     *
     * @param mobile 手机号
     */
    @GetMapping(value = "/users-anon/mobile", params = "mobile" )
    @ApiOperation(value = "根据手机号查询用户" )
    public SysUserDTO findByMobile(String mobile)
    {
        LoginAppUser loginAppUser = appUserService.findByMobile(mobile);
        return loginAppUser;

    }

    /**
     * 根据OpenId查询用户信息
     *
     * @param openId openId
     */
    @GetMapping(value = "/users-anon/openId", params = "openId" )
    @ApiOperation(value = "根据OpenId查询用户" )
    public SysUserDTO findByOpenId(String openId)
    {
        LoginAppUser loginAppUser = appUserService.findByOpenId(openId);
        return loginAppUser;
    }

    @GetMapping("/users/{id}" )
    public SysUserDTO findUserById(@PathVariable Long id)
    {
        return appUserService.getById(id);
    }

    /**
     * 管理控制台修改用户
     *
     * @param dto
     */
    @PutMapping("/users" )
    @CachePut(value = "user", key = "#dto.username" )
    public void updateSysUser(@RequestBody SysUserDTO dto)
    {
        appUserService.updateById(dto);
    }

    /**
     * 管理控制台给用户分配角色
     *
     * @param id
     * @param roleIds
     */
    @PostMapping("/users/{id}/roles" )
    public void setRoleToUser(@PathVariable Long id, @RequestBody Set<Long> roleIds)
    {
        appUserService.setRoleToUser(id, roleIds);
    }

    /**
     * 获取用户的角色
     *
     * @param
     * @return
     */
    @GetMapping("/users/{id}/roles" )
    public List<SysRolePO> findRolesByUserId(@PathVariable Long id)
    {
        return appUserService.findRolesByUserId(id);
    }

    /**
     * 用户查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "用户查询列表" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer" ),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer" )
    })
    @GetMapping("/users" )
    public PageOut<SysUserDTO> findUsers(@RequestParam Map<String, Object> params)
    {
        if (params.get("searchKey" ) != null)
        {
            params.put(String.valueOf(params.get("searchKey" )), params.get("searchValue" ));
        }
        return appUserService.findUsers(params);
    }

    /**
     * 修改用户状态
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "修改用户状态" )
    @GetMapping("/users/updateEnabled" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer" ),
            @ApiImplicitParam(name = "enabled", value = "是否启用", required = true, dataType = "Boolean" )
    })
    public RestOut<SysUserDTO> updateEnabled(@RequestParam Map<String, Object> params)
    {
        Long id = MapUtils.getLong(params, "id" );
        if (checkAdmin(id))
        {
            return RestOut.failed(ADMIN_CHANGE_MSG);
        }
        return appUserService.updateEnabled(params);
    }

    /**
     * 管理控制台，给用户重置密码
     *
     * @param id
     */
    @PutMapping(value = "/users/{id}/password" )
    public RestOut resetPassword(@PathVariable Long id)
    {
        if (checkAdmin(id))
        {
            return RestOut.failed(ADMIN_CHANGE_MSG);
        }
        appUserService.updatePassword(id, null, null);
        return RestOut.succeed("重置成功" );
    }

    /**
     * 用户自己修改密码
     */
    @PutMapping(value = "/users/password" )
    public RestOut resetPassword(@RequestBody SysUserDTO sysUserPO)
    {
        if (checkAdmin(sysUserPO.getId()))
        {
            return RestOut.failed(ADMIN_CHANGE_MSG);
        }
        appUserService.updatePassword(sysUserPO.getId(), sysUserPO.getOldPassword(), sysUserPO.getNewPassword());
        return RestOut.succeed("重置成功" );
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @DeleteMapping(value = "/users/{id}" )
    public RestOut delete(@PathVariable Long id)
    {
        if (checkAdmin(id))
        {
            return RestOut.failed(ADMIN_CHANGE_MSG);
        }
        appUserService.delUser(id);
        return RestOut.succeed("删除成功" );
    }


    /**
     * 新增or更新
     *
     * @param sysUserPO
     * @return
     */
    @CacheEvict(value = "user", key = "#sysUserPO.username" )
    @PostMapping("/users/saveOrUpdate" )
    public RestOut saveOrUpdate(@RequestBody SysUserDTO sysUserPO)
    {
        return appUserService.saveOrUpdateUser(sysUserPO);
    }


    @ApiOperation(value = "用户全文搜索列表" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "分页起始位置", required = true, dataType = "Integer" ),
            @ApiImplicitParam(name = "limit", value = "分页结束位置", required = true, dataType = "Integer" ),
            @ApiImplicitParam(name = "queryStr", value = "搜索关键字", dataType = "String" )
    })
    @GetMapping("/users/search" )
    public PageOut<JSONObject> search(SearchDto searchDto)
    {
        searchDto.setIsHighlighter(true);
        searchDto.setSortCol("createTime" );
//        return queryService.strQuery("sys_user", searchDto, SEARCH_LOGIC_DEL_DTO);

        return null;
    }

    /**
     * 是否超级管理员
     */
    private boolean checkAdmin(long id)
    {
        return id == 1L;
    }
}
