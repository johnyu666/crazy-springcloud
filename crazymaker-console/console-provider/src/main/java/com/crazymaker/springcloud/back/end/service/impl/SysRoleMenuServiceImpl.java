package com.crazymaker.springcloud.back.end.service.impl;

import com.crazymaker.springcloud.back.end.dao.SysMenuDao;
import com.crazymaker.springcloud.back.end.dao.SysRoleDao;
import com.crazymaker.springcloud.back.end.dao.SysRoleMenuDao;
import com.crazymaker.springcloud.back.end.dao.po.SysMenuPO;
import com.crazymaker.springcloud.back.end.dao.po.SysRoleMenuPO;
import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
import com.crazymaker.springcloud.common.constants.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zlt
 */
@Slf4j
@Service
public class SysRoleMenuServiceImpl
{
    @Resource
    private SysRoleMenuDao sysRoleMenuDao;

    @Resource
    private SysMenuDao sysMenuDao;
    @Resource
    private SysRoleDao roleDao;

    public void save(Long roleId, Long menuId)
    {
        SysRoleMenuPO roleMenu =
                SysRoleMenuPO.builder().roleId(roleId).menuId(menuId).build();
        sysRoleMenuDao.save(roleMenu);
    }


    public int delete(Long roleId, Long menuId)
    {
        sysRoleMenuDao.deleteByRoleIdAndAndMenuId(roleId, menuId);

        return CommonConstant.OK;
    }


    public List<SysMenuPO> findMenusByRoleIds(Iterable<Long> roleIds, Integer type)
    {

    /*    List<SysRoleMenuPO> l = sysRoleMenuDao.findAllByRoleIdIn(roleIds);

        if (null == l) {
            return new LinkedList<>();
        }
        List<Long> roleIdList = l.stream().map(po -> po.getRoleId()).collect(Collectors.toList());
*/

        return sysMenuDao.loadSysMenuListByRoles(roleIds, type);
    }


    public List<SysMenuPO> findMenusByRoleCodes(Set<String> roleCodes, Integer type)
    {
        List<SysRolePO> roleList = roleDao.findAllByCode(roleCodes);

        List<Long> roleIdList = roleList.stream().map(po -> po.getId()).collect(Collectors.toList());

        return findMenusByRoleIds(roleIdList, type);

    }

    public void saveBatch(List<SysRoleMenuPO> roleMenus)
    {

    }
}
