package com.crazymaker.springcloud.back.end.dao;

import com.crazymaker.springcloud.back.end.dao.po.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface SysClientDao extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client>
{


    /**
     * 根据 clientId 做查询
     *
     * @param clientId
     * @return 列表
     */
    List<Client> findAllByClientId(String clientId);

}
