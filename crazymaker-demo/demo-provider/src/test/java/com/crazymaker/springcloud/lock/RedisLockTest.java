package com.crazymaker.springcloud.lock;

import com.crazymaker.springcloud.demo.start.DemoCloudApplication;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoCloudApplication.class})
// 指定启动类
public class RedisLockTest
{

    @Resource
    RedisLockService redisLockService;

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    @Test
    public void testLock()
    {
        AtomicInteger count = new AtomicInteger();
        int top = 10;
        CountDownLatch countDownLatch = new CountDownLatch(top);
        for (int i = 0; i < top; i++)
        {
            pool.submit(() ->
            {
                String lockValue = UUID.randomUUID().toString();

                try
                {
                    boolean locked = redisLockService.acquire("test:lock:1", lockValue, 10, TimeUnit.SECONDS);

                    if (locked)
                    {
                        for (int j = 0; j < 1000; j++)
                        {
                            count.incrementAndGet();
                        }

                        log.info("count = " + count.get());
                        redisLockService.release("test:lock:1", lockValue);
                    }


                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }

        try
        {
            countDownLatch.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        log.info("限制的次数为：" + count.get());

        try
        {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
