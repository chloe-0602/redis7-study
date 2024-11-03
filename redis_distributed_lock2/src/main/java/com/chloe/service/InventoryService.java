package com.chloe.service;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ClassName: InventoryService
 * Package: com.chloe.service
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/3 9:56
 * @Version 1.0
 */
@Service
@Slf4j
public class InventoryService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Value("${server.port}")
    private String port;
    private Lock lock = new ReentrantLock();
    private static final String INVENTORY_KEY_01 = "inventory001";

    /**
     * 6. lua保证原子性： finally块的判断 + del删除 不是原子性
     *
     * @return
     */
    public String sale() {
        String message = "";
        String redisLockKey = "chloeRedisLock";
        String uuidValue = IdUtil.randomUUID() + ":" + Thread.currentThread().getId();


        // 自旋
        while (!stringRedisTemplate.opsForValue().setIfAbsent(redisLockKey, uuidValue, 30L, TimeUnit.SECONDS)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        stringRedisTemplate.expire(redisLockKey,20L, TimeUnit.SECONDS);

        try {
            String inventoryNumberStr = stringRedisTemplate.opsForValue().get(INVENTORY_KEY_01);
            Integer inventoryNum = inventoryNumberStr == null ? 0 : Integer.parseInt(inventoryNumberStr);
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set(INVENTORY_KEY_01, String.valueOf(--inventoryNum));
                message = "成功卖出一个商品，剩余：" + inventoryNum;
                log.info(message);
            } else {
                message = "商品卖完了......";
                log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /**
             * 使用lua脚本实现，通过判断是否存在这个value的时候 后删除这个key的 原子性
             * ！！注意，DefaultRedisScript需要加上返回的 Boolean.class 否则会报错
             */
            String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del',KEYS[1]) " +
                    "else " +
                    "return 0 " +
                    "end";
            stringRedisTemplate.execute(new DefaultRedisScript<>(luaScript, Boolean.class),
                    Arrays.asList(redisLockKey),
                    uuidValue);
        }

        return message + "\t" + "服务端口号：" + port;
    }


    /**
     * 5. 防止误删key -》 对应 【不乱抢】
     *
     * @return
     */
    public String saleV5() {
        String message = "";
        String redisLockKey = "chloeRedisLock";
        String uuidValue = IdUtil.randomUUID() + ":" + Thread.currentThread().getId();

        // 自旋
        while (!stringRedisTemplate.opsForValue().setIfAbsent(redisLockKey, uuidValue, 30L, TimeUnit.SECONDS)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        stringRedisTemplate.expire(redisLockKey,20L, TimeUnit.SECONDS);

        try {
            String inventoryNumberStr = stringRedisTemplate.opsForValue().get(INVENTORY_KEY_01);
            Integer inventoryNum = inventoryNumberStr == null ? 0 : Integer.parseInt(inventoryNumberStr);
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set(INVENTORY_KEY_01, String.valueOf(--inventoryNum));
                message = "成功卖出一个商品，剩余：" + inventoryNum;
                log.info(message);
            } else {
                message = "商品卖完了......";
                log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // v5.0判断加锁与解锁是不是同一个客户端，同一个才行，自己只能删除自己的锁，不误删他人的
            if (uuidValue.equals(stringRedisTemplate.opsForValue().get(redisLockKey))) {
                stringRedisTemplate.delete(redisLockKey);
            }
        }

        return message + "\t" + "服务端口号：" + port;
    }


    /**
     * V4.1 -》 V4.2
     * 宕机与过期 + 防止死锁
     * 解决方案： 加入一个过期时间
     * <p>
     * V4.2 设置key+过期时间需要合并成一个，具有原子性！！
     *
     * @return
     */
    public String saleV4() {
        String message = "";
        String redisLockKey = "chloeRedisLock";
        String uuidValue = IdUtil.randomUUID() + ":" + Thread.currentThread().getId();

        // 自旋
        while (!stringRedisTemplate.opsForValue().setIfAbsent(redisLockKey, uuidValue, 30L, TimeUnit.SECONDS)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        stringRedisTemplate.expire(redisLockKey,20L, TimeUnit.SECONDS);

        try {
            String inventoryNumberStr = stringRedisTemplate.opsForValue().get(INVENTORY_KEY_01);
            Integer inventoryNum = inventoryNumberStr == null ? 0 : Integer.parseInt(inventoryNumberStr);
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set(INVENTORY_KEY_01, String.valueOf(--inventoryNum));
                message = "成功卖出一个商品，剩余：" + inventoryNum;
                log.info(message);
            } else {
                message = "商品卖完了......";
                log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stringRedisTemplate.delete(redisLockKey);
        }

        return message + "\t" + "服务端口号：" + port;
    }

    /**
     * 分布式锁 修改版本3.2，
     * 1. 使用while代替if
     * 2. 想想源码中的自旋
     * 3. 注意while里面不需要加递归的代码
     *
     * @return
     */
    public String saleV32() {
        String message = "";
        String redisLockKey = "chloeRedisLock";
        String uuidValue = IdUtil.randomUUID() + ":" + Thread.currentThread().getId();

        // 自旋
        while (!stringRedisTemplate.opsForValue().setIfAbsent(redisLockKey, uuidValue)) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            String inventoryNumberStr = stringRedisTemplate.opsForValue().get(INVENTORY_KEY_01);
            Integer inventoryNum = inventoryNumberStr == null ? 0 : Integer.parseInt(inventoryNumberStr);
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set(INVENTORY_KEY_01, String.valueOf(--inventoryNum));
                message = "成功卖出一个商品，剩余：" + inventoryNum;
                log.info(message);
            } else {
                message = "商品卖完了......";
                log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stringRedisTemplate.delete(redisLockKey);
        }

        return message + "\t" + "服务端口号：" + port;
    }


    /**
     * 修改版本V3.1： 使用递归重试
     * 注意，测试的时候 saleV31() 修改为sale() 其他的sale() 方法名字改掉， 避免递归不对
     *
     * @return
     */
    public String saleV31() {
        String message = "";
        String redisLockKey = "chloeRedisLock";
        String uuidValue = IdUtil.randomUUID() + ":" + Thread.currentThread().getId();

//        log.info("--> redisLock --> Key: {} --> Value: {}",redisLockKey, uuidValue);

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(redisLockKey, uuidValue);
        if (!flag) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            sale();
        } else {
            try {
                String inventoryNumberStr = stringRedisTemplate.opsForValue().get(INVENTORY_KEY_01);
                Integer inventoryNum = inventoryNumberStr == null ? 0 : Integer.parseInt(inventoryNumberStr);
                if (inventoryNum > 0) {
                    stringRedisTemplate.opsForValue().set(INVENTORY_KEY_01, String.valueOf(--inventoryNum));
                    message = "成功卖出一个商品，剩余：" + inventoryNum;
                    log.info(message);
                } else {
                    message = "商品卖完了......";
                    log.info(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stringRedisTemplate.delete(redisLockKey);
            }
        }
        return message + "\t" + "服务端口号：" + port;
    }

    public String saleV2() {
        String message = "";

        lock.lock();
        try {
            String inventoryNumberStr = stringRedisTemplate.opsForValue().get(INVENTORY_KEY_01);
            Integer inventoryNum = inventoryNumberStr == null ? 0 : Integer.parseInt(inventoryNumberStr);
            if (inventoryNum > 0) {
                stringRedisTemplate.opsForValue().set(INVENTORY_KEY_01, String.valueOf(--inventoryNum));
                message = "成功卖出一个商品，剩余：" + inventoryNum;
                log.info(message);
            } else {
                message = "商品卖完了......";
                log.info(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return message + "\t" + "服务端口号：" + port;
    }
}
