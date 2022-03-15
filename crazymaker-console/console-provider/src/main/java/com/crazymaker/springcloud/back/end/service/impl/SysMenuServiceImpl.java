package com.crazymaker.springcloud.back.end.service.impl;

import com.crazymaker.springcloud.back.end.dao.SysMenuDao;
import com.crazymaker.springcloud.back.end.dao.po.SysMenuPO;
import com.crazymaker.springcloud.back.end.dao.po.SysRoleMenuPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
public class SysMenuServiceImpl
{
    @Resource
    private SysRoleMenuServiceImpl roleMenuService;
    @Resource
    private SysMenuDao sysMenuDao;

    @Transactional(rollbackFor = Exception.class)

    public void setMenuToRole(Long roleId, Set<Long> menuIds)
    {
        roleMenuService.delete(roleId, null);

        if (!CollectionUtils.isEmpty(menuIds))
        {
            List<SysRoleMenuPO> roleMenus = new ArrayList<>(menuIds.size());
            menuIds.forEach(menuId -> roleMenus.add(
                    SysRoleMenuPO.builder().roleId(roleId).menuId(menuId).build()));
            roleMenuService.saveBatch(roleMenus);
        }
    }

    /**
     * 角色菜单列表
     *
     * @param roleIds
     * @return
     */

    public List<SysMenuPO> findByRoles(Set<Long> roleIds)
    {
        return roleMenuService.findMenusByRoleIds(roleIds, null);
    }

    /**
     * 角色菜单列表
     *
     * @param roleIds 角色ids
     * @param roleIds 是否菜单
     * @return
     */

    public List<SysMenuPO> findByRoles(Set<Long> roleIds, Integer type)
    {
        return roleMenuService.findMenusByRoleIds(roleIds, type);
    }


    public List<SysMenuPO> findByRoleCodes(Set<String> roleCodes, Integer type)
    {
        return roleMenuService.findMenusByRoleCodes(roleCodes, type);
    }

    /**
     * 查询所有菜单
     */

    public List<SysMenuPO> findAll()
    {
        Sort order = new Sort(Sort.Direction.DESC, "sort" );
//        Sort order = new Sort(Sort.Direction.DESC, "updateTime");
        return sysMenuDao.findAll(order);

    }

    /**
     * 查询所有一级菜单
     */

    public List<SysMenuPO> findOnes()
    {
        Sort order = new Sort(Sort.Direction.DESC, "sort" );
        SysMenuPO menu = new SysMenuPO();
        menu.setType(1);
        Example<SysMenuPO> example = Example.of(menu);
        return sysMenuDao.findAll(example, order);

    }

    public void removeById(Long id)
    {
        sysMenuDao.deleteById(id);
    }

    public void saveOrUpdate(SysMenuPO menu)
    {
        sysMenuDao.save(menu);
    }
}
