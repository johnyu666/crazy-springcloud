package com.crazymaker.springcloud.back.end.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.crazymaker.springcloud.back.end.annotation.LoginUser;
import com.crazymaker.springcloud.back.end.dao.po.SysMenuPO;
import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
import com.crazymaker.springcloud.back.end.service.impl.SysMenuServiceImpl;
import com.crazymaker.springcloud.base.dao.po.SysUserPO;
import com.crazymaker.springcloud.common.constants.CommonConstant;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.context.SessionHolder;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(tags = "菜单模块api" )
@Slf4j
@RequestMapping("/menus" )
public class SysMenuController
{
    @Autowired
    private SysMenuServiceImpl menuService;

    /**
     * 两层循环实现建树
     *
     * @param sysMenuPOS
     * @return
     */
    public static List<SysMenuPO> treeBuilder(List<SysMenuPO> sysMenuPOS)
    {
        List<SysMenuPO> menus = new ArrayList<>();
        for (SysMenuPO sysMenuPO : sysMenuPOS)
        {
            if (ObjectUtils.equals(-1L, sysMenuPO.getParentId()))
            {
                menus.add(sysMenuPO);
            }
            for (SysMenuPO menu : sysMenuPOS)
            {
                if (menu.getParentId().equals(sysMenuPO.getId()))
                {
                    if (sysMenuPO.getSubMenus() == null)
                    {
                        sysMenuPO.setSubMenus(new ArrayList<>());
                    }
                    sysMenuPO.getSubMenus().add(menu);
                }
            }
        }
        return menus;
    }

    /**
     * 删除菜单
     *
     * @param id
     */
    @ApiOperation(value = "删除菜单" )
    @DeleteMapping("/{id}" )
    public RestOut<String> delete(@PathVariable Long id)
    {
        try
        {
            menuService.removeById(id);
            return RestOut.succeed("操作成功" );
        } catch (Exception ex)
        {
            log.error("memu-delete-error", ex);
            return RestOut.failed("操作失败" );
        }
    }

    @ApiOperation(value = "根据roleId获取对应的菜单" )
    @GetMapping("/{roleId}/menus" )
    public List<Map<String, Object>> findMenusByRoleId(@PathVariable Long roleId)
    {
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(roleId);
        //获取该角色对应的菜单
        List<SysMenuPO> roleMenus = menuService.findByRoles(roleIds);
        //全部的菜单列表
        List<SysMenuPO> allMenus = menuService.findAll();
        List<Map<String, Object>> authTrees = new ArrayList<>();

        Map<Long, SysMenuPO> roleMenusMap = roleMenus.stream().collect(Collectors.toMap(SysMenuPO::getId, SysMenu -> SysMenu));

        for (SysMenuPO sysMenuPO : allMenus)
        {
            Map<String, Object> authTree = new HashMap<>();
            authTree.put("id", sysMenuPO.getId());
            authTree.put("name", sysMenuPO.getName());
            authTree.put("pId", sysMenuPO.getParentId());
            authTree.put("open", true);
            authTree.put("checked", false);
            if (roleMenusMap.get(sysMenuPO.getId()) != null)
            {
                authTree.put("checked", true);
            }
            authTrees.add(authTree);
        }
        return authTrees;
    }

    @ApiOperation(value = "根据roleCodes获取对应的权限" )
    @SuppressWarnings("unchecked" )
    @Cacheable(value = "menu", key = "#roleCodes" )
    @GetMapping("/{roleCodes}" )
    public List<SysMenuPO> findMenuByRoles(@PathVariable String roleCodes)
    {
        List<SysMenuPO> RestOut = null;
        if (StringUtils.isNotEmpty(roleCodes))
        {
            Set<String> roleSet = (Set<String>) Convert.toCollection(HashSet.class, String.class, roleCodes);
            RestOut = menuService.findByRoleCodes(roleSet, CommonConstant.PERMISSION);
        }
        return RestOut;
    }

    /**
     * 给角色分配菜单
     */
    @ApiOperation(value = "角色分配菜单" )
    @PostMapping("/granted" )
    public RestOut<String> setMenuToRole(@RequestBody SysMenuPO sysMenuPO)
    {
        menuService.setMenuToRole(sysMenuPO.getRoleId(), sysMenuPO.getMenuIds());
        return RestOut.succeed("操作成功" );
    }

    @ApiOperation(value = "查询所有菜单" )
    @GetMapping("/findAlls" )
    public PageOut<SysMenuPO> findAlls()
    {
        List<SysMenuPO> list = menuService.findAll();
        return PageOut.<SysMenuPO>builder().data(list).code(0).count((long) list.size()).build();
    }

    @ApiOperation(value = "获取菜单以及顶级菜单" )
    @GetMapping("/findOnes" )
    public PageOut<SysMenuPO> findOnes()
    {
        List<SysMenuPO> list = menuService.findOnes();
        return PageOut.<SysMenuPO>builder().data(list).code(0).count((long) list.size()).build();
    }

    /**
     * 添加菜单 或者 更新
     *
     * @param menu
     * @return
     */
    @ApiOperation(value = "新增菜单" )
    @PostMapping("saveOrUpdate" )
    public RestOut saveOrUpdate(@RequestBody SysMenuPO menu)
    {
        try
        {
            menuService.saveOrUpdate(menu);
            return RestOut.succeed("操作成功" );
        } catch (Exception ex)
        {
            log.error("memu-saveOrUpdate-error", ex);
            return RestOut.failed("操作失败" );
        }
    }

    /**
     * 当前登录用户的菜单
     *
     * @return
     */
    @GetMapping("/current" )
    @ApiOperation(value = "查询当前用户菜单" )
    public List<SysMenuPO> findMyMenu(@LoginUser SysUserPO user)
    {
        String roleJson = SessionHolder.get(SessionConstants.SYS_ROLES_JSON);
        List<SysRolePO> roles = JsonUtil.jsonToPojo(roleJson, new TypeToken<List<SysRolePO>>()
        {
        }.getType());
        if (CollectionUtil.isEmpty(roles))
        {
            return Collections.emptyList();
        }
        Set<String> roleCodes = roles.parallelStream().map(SysRolePO::getCode).collect(Collectors.toSet());
        List<SysMenuPO> menus = menuService.findByRoleCodes(roleCodes, CommonConstant.MENU);
        return treeBuilder(menus);
    }
}
