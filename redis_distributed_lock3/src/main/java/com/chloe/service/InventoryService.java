package com.chloe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

    public String sale() {
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
