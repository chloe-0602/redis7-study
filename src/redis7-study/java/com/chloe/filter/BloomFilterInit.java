package com.chloe.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ClassName: BloomFilterInit
 * Package: com.chloe.filter
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 18:59
 * @Version 1.0
 */
@Component
@Slf4j
public class BloomFilterInit {
    @Autowired
    private RedisTemplate redisTemplate;
    private final static String WHITELIST_KEY = "whitelistCustomer";
    private final static String customerIdKey = "customer:13";
    @PostConstruct
    public void init(){
        Integer hashCode = Math.abs(customerIdKey.hashCode());
        Long index = (long)(hashCode % Math.pow(2, 32));

        log.info("customerId: {}, 对应的index是： {}", customerIdKey, index);

        redisTemplate.opsForValue().setBit(WHITELIST_KEY, index, true);
    }
}
