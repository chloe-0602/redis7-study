package com.chloe.redlock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ClassName: ${NAME}
 * Package: com.chloe.redlock
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/4 16:33
 * @Version 1.0
 */
@SpringBootApplication
public class RedisRedlockApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisRedlockApplication.class, args);
    }
}