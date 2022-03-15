package com.crazymaker.springcloud.back.end.service.impl;

import com.crazymaker.springcloud.back.end.dao.SysRoleDao;
import com.crazymaker.springcloud.back.end.dao.SysUserRoleDao;
import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
import com.crazymaker.springcloud.back.end.dao.po.SysUserRolePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.crazymaker.springcloud.common.constants.CommonConstant.OK;

/**
 * @author zlt
 */
@Slf4j
@Service
public class SysUserRoleServiceImpl
{
    @Resource
    private SysUserRoleDao sysUserRoleDao;

    @Resource
    private SysRoleDao roleDao;


    public int deleteUserRole(Long userId, Long roleId)
    {

        if (null == roleId)
        {
            sysUserRoleDao.deleteByUserId(userId);
            return OK;
        }
        if (null == userId)
        {
            sysUserRoleDao.deleteByRoleId(roleId);
            return OK;
        }

        sysUserRoleDao.deleteByRoleIdAndUserId(roleId, userId);
        return OK;
    }


    public int saveUserRoles(Long userId, Long roleId)
    {
        SysUserRolePO po = SysUserRolePO.builder().userId(userId).roleId(roleId).build();
        sysUserRoleDao.save(po);
        return OK;
    }


    public List<SysRolePO> findRolesByUserId(Long userId)
    {

        return roleDao.loadRoleListByUserIds(Collections.singletonList(userId));

    }


    public List<SysRolePO> findRolesByUserIds(List<Long> userIds)
    {
        return roleDao.loadRoleListByUserIds(userIds);
    }
}
