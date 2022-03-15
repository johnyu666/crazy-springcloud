package com.crazymaker.springcloud.back.end.dao;

import com.crazymaker.springcloud.back.end.dao.po.SysRoleMenuPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleMenuDao extends JpaRepository<SysRoleMenuPO, Long>, JpaSpecificationExecutor<SysRoleMenuPO>
{

    List<SysRoleMenuPO> findAllByRoleId(Iterable<Long> iterable);

    List<SysRoleMenuPO> findAllByMenuId(Iterable<Long> iterable);


    void deleteByRoleIdAndAndMenuId(Long roleId, Long menuId);


    void deleteByMenuId(Long menuId);


    void deleteByRoleId(Long roleId);

}
