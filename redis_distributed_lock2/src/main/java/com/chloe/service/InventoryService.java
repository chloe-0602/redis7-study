package com.chloe.service;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
     * 分布式锁 修改版本3.2，
     *    1. 使用while代替if
     *    2. 想想源码中的自旋
     *    3. 注意while里面不需要加递归的代码
     * @return
     */
    public String sale() {
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
