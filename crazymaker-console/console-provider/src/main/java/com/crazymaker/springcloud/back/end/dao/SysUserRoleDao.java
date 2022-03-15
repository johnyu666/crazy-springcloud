package com.crazymaker.springcloud.back.end.dao;

import com.crazymaker.springcloud.back.end.dao.po.SysUserRolePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRoleDao extends JpaRepository<SysUserRolePO, Long>, JpaSpecificationExecutor<SysUserRolePO>
{

    List<SysUserRolePO> findAllByRoleIdIn(Iterable<Long> iterable);

    List<SysUserRolePO> findAllByUserIdIn(Iterable<Long> iterable);

    void deleteByRoleIdAndUserId(Long roleId, Long userId);


    void deleteByUserId(Long userId);


    void deleteByRoleId(Long roleId);


}
