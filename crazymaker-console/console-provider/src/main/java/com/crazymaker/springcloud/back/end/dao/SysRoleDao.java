package com.crazymaker.springcloud.back.end.dao;

import com.crazymaker.springcloud.back.end.dao.po.SysRolePO;
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
public interface SysRoleDao extends JpaRepository<SysRolePO, Long>, JpaSpecificationExecutor<SysRolePO>
{


    List<SysRolePO> findAllByCode(Iterable<String> codes);


    @Query(nativeQuery = false,
            value = "select t2 from SysUserRolePO  t1 inner join SysRolePO t2 on t1.userId=t2.id  where   t1.userId in (:userIds) " )
    List<SysRolePO> loadRoleListByUserIds(@Param("userIds" ) List<Long> userIds);

}
