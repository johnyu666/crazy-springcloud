package com.crazymaker.springcloud.standard.lock;

import cn.hutool.core.io.FileUtil;
import com.crazymaker.springcloud.common.util.IOUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Slf4j
@Data
public class RedisLockService
{
    /**
     * 默认为1000ms
     */
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final Long LOCKED = Long.valueOf(1);
    public static final Long UNLOCKED = Long.valueOf(1);
    public static final Long WAIT_GAT = Long.valueOf(200);
    public static final int EXPIRE = 200;


    private RedisTemplate redisTemplate;


//    static String lockLua = "classpath:script/lock.lua";
//    static String unLockLua = "classpath:script/unlock.lua";

    static String lockLua = "script/lock.lua";
    static String unLockLua = "script/unlock.lua";
    static RedisScript<Long> lockScript = null;
    static RedisScript<Long> unLockScript = null;

    {
        String script = IOUtil.loadJarFile(RedisLockService.class.getClassLoader(),lockLua);
//        String script = FileUtil.readString(lockLua, Charset.forName("UTF-8" ));
        if(StringUtils.isEmpty(script))
        {
            log.error("lua load failed:"+lockLua);
        }

        lockScript = new DefaultRedisScript<>(script, Long.class);



//        script = FileUtil.readString(unLockLua, Charset.forName("UTF-8" ));
        script =  IOUtil.loadJarFile(RedisLockService.class.getClassLoader(),unLockLua);
        if(StringUtils.isEmpty(script))
        {
            log.error("lua load failed:"+unLockLua);
        }
        unLockScript = new DefaultRedisScript<>(script, Long.class);

    }

    public RedisLockService(RedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取一个分布式锁 , 超时则返回失败
     *
     * @param lockValue 锁的value
     * @return 获锁成功 - true | 获锁失败 - false
     */
    public boolean acquire(String key, String lockValue, int timeout, TimeUnit unit)
    {
        long startMillis = System.currentTimeMillis();
        Long millisToWait = unit != null ? unit.toMillis(timeout) : DEFAULT_TIMEOUT;

        boolean isLocked = false;

        while (!isLocked)
        {
            try
            {
                boolean locked = this.lock(lockValue, key, EXPIRE);
                if (!locked)
                {
                    millisToWait = millisToWait - (System.currentTimeMillis() - startMillis);
                    startMillis = System.currentTimeMillis();
                    isLocked = false;

                    if (millisToWait > 0L)
                    {
                        millisToWait = WAIT_GAT;
                        /**
                         * 还没有超时
                         */
                        this.wait(millisToWait);
                    } else
                    {
                        this.release(lockValue, key);
                        break;
                    }
                } else
                {
                    isLocked = true;

                }

            } catch (InterruptedException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return isLocked;
    }

    public boolean lock(String key, String lockValue, int expire)
    {
        if (null == key)
        {
            return false;
        }
        try
        {
            List<String> redisKeys = new ArrayList<>();
            redisKeys.add(key);
            redisKeys.add(lockValue);
            redisKeys.add(String.valueOf(expire));

            Long res = (Long) redisTemplate.execute(lockScript, redisKeys);


            return res != null && res.equals(LOCKED);
        } catch (Exception e)
        {
            return false;
        }
    }

    public boolean release(String key, String lockValue)
    {
        if (key == null || lockValue == null)
        {
            return false;
        }
        try
        {
            List<String> redisKeys = new ArrayList<>();
            redisKeys.add(key);
            redisKeys.add(lockValue);
            Long res = (Long) redisTemplate.execute(unLockScript, redisKeys);

            boolean unlocked = res != null && res.equals(UNLOCKED);

            if (unlocked)
            {
                this.notify();
            }
            return unlocked;
        } catch (Exception e)
        {
            return false;
        }
    }


}
