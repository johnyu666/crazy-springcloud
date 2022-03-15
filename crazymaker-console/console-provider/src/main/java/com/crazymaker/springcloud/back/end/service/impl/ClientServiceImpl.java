package com.crazymaker.springcloud.back.end.service.impl;


import com.crazymaker.springcloud.back.end.api.dto.ClientDto;
import com.crazymaker.springcloud.back.end.dao.SysClientDao;
import com.crazymaker.springcloud.back.end.dao.po.Client;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.MapUtil;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author zlt
 */
@Slf4j
@Service
public class ClientServiceImpl
{

    @Autowired
    private RedisRepository redisRepository;

    @Resource
    SysClientDao sysClientDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RestOut<String> saveClient(ClientDto dto)
    {

        Client client = new Client();
        try
        {
            BeanUtils.copyProperties(dto, client);
            sysClientDao.save(client);

            return RestOut.succeed("操作成功" );
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return RestOut.failed("操作失败" );
    }

    public PageOut<Client> listClent(Map<String, Object> params, boolean isPage)
    {

        PageRequest jpaPage = null;
        if (isPage)
        {
            Integer curPage = MapUtils.getInteger(params, "page" );
            Integer limit = MapUtils.getInteger(params, "limit" );

            jpaPage = PageRequest.of(curPage - 1, limit);
        } else
        {
            jpaPage = PageRequest.of(0, Integer.MAX_VALUE);
        }
        Client example = MapUtil.map2Object(params, Client.class);

        /**
         * 创建条件对象
         */
        Page<Client> page = null;
        if (null == example)
        {
            page = sysClientDao.findAll(jpaPage);
        } else
        {
            page = sysClientDao.findAll(Example.of(example), jpaPage);
        }


        List<Client> list = page.getContent();
        long total = page.getTotalElements();
        return PageOut.<Client>builder().data(list).code(0).count(total).build();
    }

    public void delClient(long id)
    {
        String clientId = sysClientDao.getOne(id).getClientId();
        sysClientDao.deleteById(id);
        redisRepository.del(clientRedisKey(clientId));
    }

    private String clientRedisKey(String clientId)
    {
        return SessionConstants.CACHE_CLIENT_KEY + ":" + clientId;
    }

    public Client getById(Long id)
    {
        return sysClientDao.getOne(id);
    }
}
