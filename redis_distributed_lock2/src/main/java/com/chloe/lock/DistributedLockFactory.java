package com.chloe.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * ClassName: DistributedLockFactory
 * Package: com.chloe.lock
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/3 22:16
 * @Version 1.0
 */
@Component
public class DistributedLockFactory {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //    private String lockType;
    private String redisLockName = "chloeRedisLock";

    public Lock getDistributedLock(String lockType) {
        if (null == lockType) {
            return null;
        }

        if (lockType.equalsIgnoreCase("REDIS")) {
            return new RedisDistributedLock(stringRedisTemplate, redisLockName);
        } else if (lockType.equalsIgnoreCase("ZOOKEEPER")) {
            // TODO ZK
        }
        return null;
    }
}
