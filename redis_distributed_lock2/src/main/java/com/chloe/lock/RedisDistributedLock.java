package com.chloe.lock;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * ClassName: RedisDistributedLock
 * Package: com.chloe.lock
 * Description:
 * 1. 直接new因为stringRedisTemplate会有空指针问题
 *
 * @Author Xu, Luqin
 * @Create 2024/11/3 21:13
 * @Version 1.0
 */
//@Component
//@Data
@Slf4j
public class RedisDistributedLock implements Lock {

    private StringRedisTemplate stringRedisTemplate;
    private String lockName;
    private String uuidValue;
    private Long expireTime;

    public RedisDistributedLock(StringRedisTemplate stringRedisTemplate, String lockName, String uuid) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.uuidValue = uuid + ":" + Thread.currentThread().getId();
        this.expireTime = 30L;
    }

    @Override
    public void lock() {
        tryLock();
    }


    @Override
    public boolean tryLock() {
        try {
            tryLock(-1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        /**
         * eval "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then   redis.call('HINCRBY',KEYS[1],ARGV[1],1) redis.call('expire',KEYS[1],ARGV[2]) return 1 else return 0 end"
         * 1 chloeRedisLock 1122:1 50
         */
        if (-1L == time) {
            String luaLockScript = "if redis.call('exists', KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then  " +
                    " redis.call('HINCRBY',KEYS[1],ARGV[1],1)" +
                    " redis.call('expire',KEYS[1],ARGV[2])" +
                    " return 1" +
                    " else " +
                    "return 0" +
                    " end";

            while (!stringRedisTemplate.execute(new DefaultRedisScript<>(luaLockScript, Boolean.class), Arrays.asList(lockName), uuidValue, String.valueOf(expireTime))) {
                TimeUnit.MILLISECONDS.sleep(50);
            }
            log.info("--> redis distributed lock: {}", uuidValue);

            renewExpire();

        } else {
            this.expireTime = unit.toSeconds(time);
        }
        return true;
    }

    private void renewExpire() {
        String renewExpireLuaScript = "if redis.call('hexists',KEYS[1],ARGV[1]) == 1 then" +
                " return redis.call('expire',KEYS[1],ARGV[2]) " +
                " else " +
                " return 0" +
                " end";
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(stringRedisTemplate.execute(
                        new DefaultRedisScript<>(renewExpireLuaScript, Boolean.class),
                        Arrays.asList(lockName),
                        uuidValue,
                        String.valueOf(expireTime))){
                    renewExpire();
                }
            }
        }, (this.expireTime * 1000) / 3);
    }

    @Override
    public void unlock() {
        /**
         * eval "if redis.call('hexists',KEYS[1],ARGV[1]) == 0 then return nil elseif redis.call('HINCRBY',KEYS[1],ARGV[1],-1) == 0 then  return redis.call('del',KEYS[1]) else return 0 end"
         * 1
         * chloeRedisLock
         * 1122:1
         */
        String luaUnlockScript = "if redis.call('hexists',KEYS[1],ARGV[1]) == 0 then" +
                " return nil" +
                " elseif redis.call('HINCRBY',KEYS[1],ARGV[1],-1) == 0 then " +
                " return redis.call('del',KEYS[1])" +
                " else" +
                " return 0 end";
        Long res = stringRedisTemplate.execute(new DefaultRedisScript<>(luaUnlockScript, Long.class), Arrays.asList(lockName), uuidValue);
        if (null == res) {
            throw new RuntimeException("---> lock: " + lockName + " not exists...");
        }
        log.info("--> redis distributed unlock: {}", uuidValue);
    }

    // 以下不做，因为我们的分布式锁，是用完之后直接删除
    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
