package com.crazymaker.springcloud.back.end.service.impl;


import com.crazymaker.springcloud.back.end.dao.SysRoleDao;
import com.crazymaker.springcloud.back.end.dao.SysRoleMenuDao;
import com.crazymaker.springcloud.back.end.dao.SysUserRoleDao;
import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class SysRoleServiceImpl
{

    @Resource
    private SysUserRoleDao sysUserRoleDao;

    @Resource
    private SysRoleMenuDao sysRoleMenuDao;


    @Resource
    private SysRoleDao sysRoleDao;


    @Transactional(rollbackFor = Exception.class)

    public void saveRole(SysRolePO sysRolePO)
    {
        sysRoleDao.save(sysRolePO);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id)
    {
        sysRoleMenuDao.deleteByRoleId(id);
        sysUserRoleDao.deleteByRoleId(id);
        sysRoleDao.deleteById(id);
    }


    public PageOut<SysRolePO> findRoles(Map<String, Object> params)
    {

        Integer curPage = MapUtils.getInteger(params, "page" );
        Integer limit = MapUtils.getInteger(params, "limit" );

        PageRequest jpaPage = PageRequest.of(curPage - 1, limit);

        SysRolePO example = MapUtil.map2Object(params, SysRolePO.class);

        /**
         * 创建条件对象
         */
        Page<SysRolePO> page = null;
        if (null == example)
        {
            page = sysRoleDao.findAll(jpaPage);
        } else
        {
            page = sysRoleDao.findAll(Example.of(example), jpaPage);
        }


        List<SysRolePO> list = page.getContent();
        long total = page.getTotalElements();
        return PageOut.<SysRolePO>builder().data(list).code(0).count(total).build();
    }

    @Transactional
    public RestOut<String> saveOrUpdateRole(SysRolePO sysRolePO)
    {
        this.saveRole(sysRolePO);
        return RestOut.succeed("操作成功" );
    }
}
