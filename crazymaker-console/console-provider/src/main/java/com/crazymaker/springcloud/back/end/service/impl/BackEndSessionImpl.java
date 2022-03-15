package com.crazymaker.springcloud.back.end.service.impl;

import com.auth0.jwt.JWT;
import com.crazymaker.springcloud.back.end.api.dto.LoginInfoDTO;
import com.crazymaker.springcloud.base.dao.SysUserDao;
import com.crazymaker.springcloud.base.dao.po.SysUserPO;
import com.crazymaker.springcloud.base.security.token.JwtAuthenticationToken;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.standard.context.AppContextHolder;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import com.crazymaker.springcloud.standard.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crazymaker.springcloud.common.context.SessionHolder.G_USER;

/**
 * Created by 尼恩 on 2019/7/18.
 */

@Slf4j
@Service
public class BackEndSessionImpl implements UserDetailsService
{

    @Resource
    private RedisOperationsSessionRepository sessionRepository;

    @Resource
    private RedisRepository redisRepository;

    @Resource
    HttpSessionIdResolver httpSessionIdResolver;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SysUserDao sysUserDao;

    static Map<Long, Object> userMap = new ConcurrentHashMap();

    public UserDTO getUser(Long userId)
    {
        SysUserPO userPO = sysUserDao.getOne(userId);
        if (userPO != null)
        {
            UserDTO dto = new UserDTO();

            BeanUtils.copyProperties(userPO, dto);

            return dto;
        }
        return null;
    }


    public BackEndSessionImpl()
    {
//        //默认使用 bcrypt， strength=10
//        this.passwordEncoder =
//                PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    private SysUserPO loadFromDB(String username)
    {
        if (null == sysUserDao)

        {
            sysUserDao = AppContextHolder.getBean(SysUserDao.class);
        }

        List<SysUserPO> list = sysUserDao.findAllByUsername(username);

        if (null == list || list.size() <= 0)
        {
            return null;
        }
        SysUserPO userPO = list.get(0);
        return userPO;
    }

    /**
     * 登录之后，保存用户信息到session
     *
     * @param user 用户
     * @return token
     */
    public String saveUserLoginInfo(UserDetails user)
    {


        String username = user.getUsername();

        SysUserPO userPO = loadFromDB(username);


        String token = setSession(userPO);


        return token;
    }




    private String setSession(SysUserPO userPO)
    {
        if (null == userPO)
        {
            throw BusinessException.builder().errMsg("用户不存在或者密码错误" ).build();
        }

        /**
         *  将TOKEN保存到数据库或者缓存中
         */

        /**
         *  根据用户id，查询之前保持的sessionid，
         *  防止频繁登录的时候，session  被大量创建
         */
        String uid = String.valueOf(userPO.getId());
        String sid = redisRepository.getSessionId(uid);


        Session session = null;

        try
        {
            /**
             * 查找现有的session
             */
            session = sessionRepository.findById(sid);
        } catch (Exception e)
        {
//            e.printStackTrace();
            log.info("查找现有的session 失败，将创建一个新的" );
        }

        if (null == session)
        {
            session = sessionRepository.createSession();
            redisRepository.setSessionId(uid, session.getId());
        }
        sid = session.getId();


        String salt = userPO.getPassword();
        String token = AuthUtils.buildToken(sid, salt);

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userPO, userDTO);
        userDTO.setToken(token);

        /**
         * 将用户信息缓存起来
         */
        session.setAttribute(G_USER, JsonUtil.pojoToJson(userDTO));


        return token;
    }




    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException
    {


        SysUserPO sysUserPO = loadFromDB(username);


        //将salt放到password字段返回
        return User.builder()
                .username(sysUserPO.getUsername())
                .password(sysUserPO.getPassword())
//                .password(SessionConstants.SALT)
                //BCrypt.gensalt();  正式开发时可以调用该方法实时生成加密的salt
//                .password(SessionConstants.SALT)
                .authorities(SessionConstants.USER_INFO)
                .roles("USER" )
                .build();

    }


    public UserDTO addUser(UserDTO dto)
    {

        SysUserPO po = new SysUserPO();
        BeanUtils.copyProperties(dto, po);
        po.setId(null);
        sysUserDao.saveAndFlush(po);
        BeanUtils.copyProperties(po, dto);
        return dto;
    }




    /**
     * 根据认证信息  登录
     *
     * @param user
     * @return
     */

    public OAuth2AccessToken login(LoginInfoDTO user)
    {

        SysUserPO userPO = loadFromDB(user.getUsername());


        String token = setSession(userPO);

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(token));

        return authToken.createAccessToken();
    }
}
