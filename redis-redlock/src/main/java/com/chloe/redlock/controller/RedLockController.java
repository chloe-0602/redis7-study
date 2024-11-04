package com.chloe.redlock.controller;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * ClassName: RedLockController
 * Package: com.chloe.redlock.controller
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/4 16:45
 * @Version 1.0
 */
@RestController
@Slf4j
public class RedLockController {
    @Autowired
    RedissonClient redissonClient1;
    @Autowired
    RedissonClient redissonClient2;
    @Autowired
    RedissonClient redissonClient3;

    private static final String MULTI_LOCK_KEY = "CHLOE_REDLOCK";
    @GetMapping(value = "/multiLock")
    public String getMultiLock() throws InterruptedException {
        String uuidValue = IdUtil.randomUUID() + ":" + Thread.currentThread().getId();

        RLock lock1 = redissonClient1.getLock(MULTI_LOCK_KEY);
        RLock lock2 = redissonClient2.getLock(MULTI_LOCK_KEY);
        RLock lock3 = redissonClient3.getLock(MULTI_LOCK_KEY);

        RedissonMultiLock redissonMultiLock = new RedissonMultiLock(lock1, lock2, lock3);

        redissonMultiLock.lock();
        try {
            log.info("----- {} enter into multilock ", uuidValue);
            try { TimeUnit.SECONDS.sleep(30); } catch (InterruptedException e) { throw new RuntimeException(e); }
            log.info("----- {} leave multilock ", uuidValue);
        }catch (Exception e){
            e.printStackTrace();
            log.info("----- {} multilock exception is: {}", uuidValue, e.getMessage());
        } finally {
            redissonMultiLock.unlock();
            log.info("----- {} unlock multilock, release lock: {} ", uuidValue, MULTI_LOCK_KEY);
        }

        return "multilock is finished " + Thread.currentThread().getId();
    }
}
