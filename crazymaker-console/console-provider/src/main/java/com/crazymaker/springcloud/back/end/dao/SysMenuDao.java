package com.crazymaker.springcloud.back.end.dao;

import com.crazymaker.springcloud.back.end.dao.po.SysMenuPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface SysMenuDao extends JpaRepository<SysMenuPO, Long>, JpaSpecificationExecutor<SysMenuPO>
{

    @Query(nativeQuery = false,
            value = "select t2 from SysRoleMenuPO t1 inner join SysMenuPO t2 on t1.menuId=t2.id  where   t1.roleId in (:roleIds) and t2.type = :type" )
    List<SysMenuPO> loadSysMenuListByRoles(@Param("roleIds" ) Iterable<Long> roleIds, @Param("type" ) Integer type);

}
