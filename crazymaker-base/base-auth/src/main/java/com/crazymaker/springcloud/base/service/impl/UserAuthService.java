package com.crazymaker.springcloud.base.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.crazymaker.springcloud.base.dao.UserDao;
import com.crazymaker.springcloud.base.dao.po.UserPO;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.context.SessionHolder;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.standard.context.AppContextHolder;
import com.crazymaker.springcloud.user.info.api.dto.LoginInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.crazymaker.springcloud.common.context.SessionHolder.G_USER;

/**
 * Created by 尼恩 on 2019/7/18.
 */

@Slf4j
@Service
public class UserAuthService implements UserDetailsService
{


    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisOperationsSessionRepository sessionRepository;


    @Resource
    HttpSessionIdResolver httpSessionIdResolver;

    @Autowired(required = false)
    private UserDao userDao;

    static Map<Long, Object> userMap = new ConcurrentHashMap();

    public UserDTO getUser(Long userId)
    {
        UserPO userPO = userDao.findByUserId(userId);
        if (userPO != null)
        {
            UserDTO dto = new UserDTO();

            BeanUtils.copyProperties(userPO, dto);

            return dto;
        }
        return null;
    }


    public UserAuthService()
    {
    }


    private UserPO loadFromDB(String username)
    {
        if (null == userDao)

        {
            userDao = AppContextHolder.getBean(UserDao.class);
        }

        List<UserPO> list = userDao.findAllByUsername(username);

        if (null == list || list.size() <= 0)
        {
            return null;
        }
        UserPO userPO = list.get(0);
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

        UserPO userPO = loadFromDB(username);


        String token = setSession(userPO);


        return token;
    }


    private String setSession(UserPO userPO)
    {
        if (null == userPO)
        {
            throw BusinessException.builder().errMsg("用户不存在或者密码错误" ).build();
        }
        String salt = userPO.getPassword();

        Algorithm algorithm = Algorithm.HMAC256(salt);
        long start = System.currentTimeMillis();


        /**
         *  将TOKEN保存到数据库或者缓存中
         */


        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userPO, userDTO);



        /**
         *  根据用户名称，查询之前保持的sessionid，
         *  防止频繁登录的时候，session  被大量创建
         */
        Object sid = SessionHolder.getSid();


        Session session = null;

        try
        {
            /**
             * 查找现有的session
             */
            session = sessionRepository.findById(sid.toString());
        } catch (Exception e)
        {
//            e.printStackTrace();
            log.info("查找现有的session 失败，将创建一个新的" );
        }

        if (null == session)
        {
            session = sessionRepository.createSession();
        }
        sid = session.getId();


        Date date = new Date(start + SessionConstants.SESSION_TIME_OUT * 1000);  //设置过期
        String token = JWT.create()
                .withSubject(session.getId())
                .withExpiresAt(date)
                .withIssuedAt(new Date(start - 60000))
                .sign(algorithm);
        userDTO.setToken(token);

        /**
         * 将用户信息缓存起来
         */
        session.setAttribute(G_USER, JsonUtil.pojoToJson(userDTO));

        return token;
    }


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException
    {


        UserPO userPO = loadFromDB(username);


        //将salt放到password字段返回
        return User.builder()
                .username(userPO.getUsername())
                .password(userPO.getPassword())
//                .password(SessionConstants.SALT)
                //BCrypt.gensalt();  正式开发时可以调用该方法实时生成加密的salt
//                .password(SessionConstants.SALT)
                .authorities(SessionConstants.USER_INFO)
                .roles("USER" )
                .build();

    }


    public UserDTO addUser(UserDTO dto)
    {

        UserPO po = new UserPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(null);
        userDao.saveAndFlush(po);
        BeanUtils.copyProperties(po, dto);
        return dto;
    }


    /**
     * 登陆
     *
     * @param user
     * @return
     */
    public String login(LoginInfoDTO user)
    {
        Map<String, String> resultMap = new HashedMap();
        String username = user.getUsername();

        UserPO userPO = loadFromDB(username);


        String token = setSession(userPO);


        return token;

    }
}
