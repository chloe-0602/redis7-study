package com.chloe.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LettuceDemo {
    public static void main(String[] args) {
        RedisURI redisURI = RedisURI.builder()
                .withHost("192.168.10.100")
                .withPort(6379)
                .withAuthentication("default", "111111")
                .build();

        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection connect = redisClient.connect();
        RedisCommands redisCommands = connect.sync();

        List keys = redisCommands.keys("*");
        log.info("keys:{}", keys);

        redisCommands.set("k1", "lettuce");
        log.info("string value, k1 :{}", redisCommands.get("k1"));

        redisCommands.lpush("list1", "a", "b", "c");
        log.info("list1:{}", redisCommands.lrange("list1", 0, -1));

        redisCommands.sadd("set1", "a", "b", "c", "a");
        log.info("set1:{}", redisCommands.smembers("set1"));

        redisCommands.hset("hash1", "name", "lettuce");
        redisCommands.hset("hash1", "age", "18");
        log.info("hash1:{}", redisCommands.hgetall("hash1"));

        redisCommands.zadd("zset1", 70, "a");
        redisCommands.zadd("zset1", 80, "b");
        redisCommands.zadd("zset1", 90, "c");
        log.info("zset1:{}", redisCommands.zrangeWithScores("zset1", 0, -1));

        connect.close();
        redisClient.shutdown();
    }
}
