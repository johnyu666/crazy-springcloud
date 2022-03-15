package com.crazymaker.springcloud.back.end.service.impl;

import cn.hutool.core.util.StrUtil;
import com.crazymaker.springcloud.back.end.api.dto.SysRoleDTO;
import com.crazymaker.springcloud.back.end.api.dto.SysUserDTO;
import com.crazymaker.springcloud.back.end.dao.SysRoleMenuDao;
import com.crazymaker.springcloud.back.end.dao.SysUserRoleDao;
import com.crazymaker.springcloud.back.end.dao.po.*;
import com.crazymaker.springcloud.base.dao.SysUserDao;
import com.crazymaker.springcloud.base.dao.po.SysUserPO;
import com.crazymaker.springcloud.common.constants.CommonConstant;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.page.DataAdapter;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.MapUtil;
import com.crazymaker.springcloud.standard.config.SysBaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysUserServiceImpl
{

    @Resource
    SysUserDao sysUserDao;


    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysUserRoleServiceImpl userRoleService;

    @Resource
    private SysRoleMenuServiceImpl roleMenuService;

    @Resource
    private SysUserRoleDao sysUserRoleDao;

    @Resource
    private SysBaseProperties sysBaseProperties;


    public LoginAppUser findByUsername(String username)
    {
        SysUserPO sysUserPO = this.selectByUsername(username);
        return loadLoginUser(sysUserPO);
    }


    public LoginAppUser findByOpenId(String username)
    {
        SysUserPO sysUserPO = this.selectByOpenId(username);
        return loadLoginUser(sysUserPO);
    }


    public LoginAppUser findByMobile(String username)
    {
        SysUserPO sysUserPO = this.selectByMobile(username);
        return loadLoginUser(sysUserPO);
    }


    public LoginAppUser getLoginAppUser(UserDTO userDTO)
    {
        if (userDTO != null)
        {
            SysUserPO sysUserPO = new SysUserPO();
            BeanUtils.copyProperties(userDTO, sysUserPO);
            return loadLoginUser(sysUserPO);
        }
        return null;
    }

    private LoginAppUser loadLoginUser(SysUserPO sysUserPO)
    {
        LoginAppUser loginAppUser = new LoginAppUser();
        BeanUtils.copyProperties(sysUserPO, loginAppUser);

        List<SysRolePO> sysRolePOS = userRoleService.findRolesByUserId(sysUserPO.getId());

        List<SysRoleDTO> roleDtos = DataAdapter.convertList(sysRolePOS, SysRoleDTO.class);

        // 设置角色
        loginAppUser.setRoles(roleDtos);

        if (!CollectionUtils.isEmpty(sysRolePOS))
        {
            Set<Long> roleIds = sysRolePOS.parallelStream().map(SysRolePO::getId).collect(Collectors.toSet());
            List<SysMenuPO> menus = roleMenuService.findMenusByRoleIds(roleIds, CommonConstant.PERMISSION);
            if (!CollectionUtils.isEmpty(menus))
            {
                Set<String> permissions = menus.parallelStream().map(p -> p.getPath())
                        .collect(Collectors.toSet());
                // 设置权限集合
                loginAppUser.setPermissions(permissions);
            }
        }
        return loginAppUser;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @return
     */

/*    public SysUserDTO findByUsername(String username) {
        List<SysUserPO> users = sysUserDao.findAll(
                Example.of(SysUserPO.builder().username(username).build()));

        SysUserPO po = getUser(users);
        return  DataAdapter.convert(po,SysUserDTO.class);
    }*/
    private SysUserPO selectByUsername(String username)
    {
        List<SysUserPO> users = sysUserDao.findAll(
                Example.of(SysUserPO.builder().username(username).build()));

        SysUserPO po = getUser(users);
        return po;
    }

    /**
     * 根据手机号查询用户
     *
     * @param mobile
     * @return
     */

    public SysUserPO selectByMobile(String mobile)
    {
        List<SysUserPO> users = sysUserDao.findAll(
                Example.of(SysUserPO.builder().mobile(mobile).build()));

        return getUser(users);
    }

    /**
     * 根据openId查询用户
     *
     * @param openId
     * @return
     */

    public SysUserPO selectByOpenId(String openId)
    {

        List<SysUserPO> users = sysUserDao.findAll(
                Example.of(SysUserPO.builder().openId(openId).build()));

        return getUser(users);
    }

    private SysUserPO getUser(List<SysUserPO> users)
    {
        SysUserPO user = null;
        if (users != null && !users.isEmpty())
        {
            user = users.get(0);
        }
        return user;
    }

    /**
     * 给用户设置角色
     */
    @Transactional(rollbackFor = Exception.class)

    public void setRoleToUser(Long id, Set<Long> roleIds)
    {
        SysUserPO sysUserPO = sysUserDao.getOne(id);
        if (sysUserPO == null)
        {
            throw new IllegalArgumentException("用户不存在" );
        }

        userRoleService.deleteUserRole(id, null);
        if (!CollectionUtils.isEmpty(roleIds))
        {
            List<SysUserRolePO> roleUsers = new ArrayList<>(roleIds.size());
            roleIds.forEach(roleId -> roleUsers.add(
                    SysUserRolePO.builder().userId(id).roleId(roleId).build()));
            sysUserRoleDao.saveAll(roleUsers);
        }
    }

    @Transactional
    public RestOut updatePassword(Long id, String oldPassword, String newPassword)
    {
        SysUserPO sysUserPO = sysUserDao.getOne(id);
        if (StrUtil.isNotBlank(oldPassword))
        {
            if (!passwordEncoder.matches(oldPassword, sysUserPO.getPassword()))
            {
                return RestOut.failed("旧密码错误" );
            }
        }
        if (StrUtil.isBlank(newPassword))
        {
            newPassword = sysBaseProperties.getSecret();
        }

        sysUserPO.setPassword(passwordEncoder.encode(newPassword));
        sysUserDao.save(sysUserPO);
        return RestOut.succeed("修改成功" );
    }


    public PageOut<SysUserDTO> findUsers(Map<String, Object> params)
    {
        Integer curPage = MapUtils.getInteger(params, "page" );
        Integer limit = MapUtils.getInteger(params, "limit" );
        PageRequest jpaPage = PageRequest.of(curPage - 1, limit);

        SysUserPO example = MapUtil.map2Object(params, SysUserPO.class);

        /**
         * 创建条件对象
         */
        Page<SysUserPO> page = null;
        if (null == example)
        {
            page = sysUserDao.findAll(jpaPage);
        } else
        {
            page = sysUserDao.findAll(Example.of(example), jpaPage);
        }

        List<SysUserPO> polist = page.getContent();
        List<SysUserDTO> list = DataAdapter.convertList(polist, SysUserDTO.class);
        long total = page.getTotalElements();
        if (total > 0)
        {
            List<Long> userIds = list.stream().map(SysUserDTO::getId).collect(Collectors.toList());

            List<SysRolePO> sysRolePOS = userRoleService.findRolesByUserIds(userIds);
            Map<Long, SysRoleDTO> roleMap = new LinkedHashMap<>();

            sysRolePOS.stream().map(role -> roleMap.put(role.getId(), DataAdapter.convert(role, SysRoleDTO.class))).count();

            List<SysUserRolePO> sysUserRolePOS = sysUserRoleDao.findAllByUserIdIn(userIds);


            list.forEach(u -> u.setRoles(
                    sysUserRolePOS.stream()
                            .filter(r -> !ObjectUtils.notEqual(u.getId(), r.getUserId()))
                            .map(r -> roleMap.get(r.getId()))
                            .collect(Collectors.toList())));
        }
        return PageOut.<SysUserDTO>builder().data(list).code(0).count(total).build();
    }


    public List<SysRolePO> findRolesByUserId(Long userId)
    {
        return userRoleService.findRolesByUserId(userId);
    }


    public RestOut<SysUserDTO> updateEnabled(Map<String, Object> params)
    {
        Long id = MapUtils.getLong(params, "id" );
        Boolean enabled = MapUtils.getBoolean(params, "enabled" );

        SysUserPO appUser = sysUserDao.getOne(id);
        if (appUser == null)
        {
            return RestOut.failed("用户不存在" );
        }
        appUser.setEnabled(enabled);
        appUser.setUpdateTime(new Date());

        sysUserDao.save(appUser);
        log.info("修改用户：{}", appUser);

        SysUserDTO sysUserDTO = DataAdapter.convert(appUser, SysUserDTO.class);
        return RestOut.success(sysUserDTO, "更新成功" );
    }

    @Transactional(rollbackFor = Exception.class)
    public RestOut<SysUserDTO> saveOrUpdateUser(SysUserDTO dto)
    {
        if (dto.getId() == null)
        {
            if (StringUtils.isBlank(dto.getType()))
            {
                dto.setType(UserType.BACKEND.name());
            }
            dto.setPassword(passwordEncoder.encode(sysBaseProperties.getSecret()));
            dto.setEnabled(Boolean.TRUE);
        }

        SysUserPO userPo = DataAdapter.convert(dto, SysUserPO.class);
        sysUserDao.saveAndFlush(userPo);

        //更新角色
        if (null != dto.getRoles() && dto.getRoles().size() > 0)
        {
            userRoleService.deleteUserRole(dto.getId(), null);
            List<SysRoleDTO> roleDTOS = dto.getRoles();
            List<SysUserRolePO> userRoleList = roleDTOS.stream().map(
                    sysRole -> SysUserRolePO.builder().roleId(sysRole.getId())
                            .userId(dto.getId()).build())
                    .collect(Collectors.toList());
            sysUserRoleDao.saveAll(userRoleList);
        }
        return RestOut.success(dto, "操作成功" );
    }

    @Transactional(rollbackFor = Exception.class)

    public boolean delUser(Long id)
    {
        userRoleService.deleteUserRole(id, null);
        sysUserDao.deleteById(id);
        return true;
    }


    public SysUserDTO getById(Long id)
    {
        SysUserPO po = sysUserDao.getOne(id);

        return DataAdapter.convert(po, SysUserDTO.class);
    }


    public void updateById(SysUserDTO dto)
    {

        SysUserPO sysUserPO = DataAdapter.convert(dto, SysUserPO.class);
        sysUserDao.save(sysUserPO);
    }


    public RestOut<UserDTO> getUserDto(Long id)
    {
        SysUserPO userPO = sysUserDao.getOne(id);
        if (userPO != null)
        {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(userPO, userDTO);
            return RestOut.success(userDTO);
        } else
        {
            return RestOut.error("未找到指定用户" );
        }
    }
}