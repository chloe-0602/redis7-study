package com.chloe.service;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * ClassName: RedPackageService
 * Package: com.chloe.service
 * Description:
 *   模拟微信红包的功能
 *   1. 发红包
 *   2. 抢红包
 *
 *   注意点： 高并发 不加锁 原子性
 * @Author Xu, Luqin
 * @Create 2024/11/5 22:27
 * @Version 1.0
 */
@Service
@Slf4j
public class EnvelopeService {
    @Autowired
    RedisTemplate redisTemplate;
    private final static String ENVELOPE_KEY = "envelop:";
    private final static String ENVELOPE_CONSUME_KEY = "envelope:consume:";

    /**
     * 模拟微信发红包功能
     * 1. 怎么拆分？ 二倍均值法
     * 2. 将数据存入Redis的list
     * @param totalMoney
     * @param count
     * @return
     */
    public String send(Integer totalMoney, Integer count) {
        Integer[] envelope = splitEnvelopeAlgorithm(totalMoney, count);
        String envelopeKey = ENVELOPE_KEY + IdUtil.randomUUID();

        redisTemplate.opsForList().leftPushAll(envelopeKey, envelope);

        log.info("发红包：{}， 具体金额信息是： {}", envelopeKey, envelope.toString());
        return "发红包："+envelopeKey+"， 具体金额信息是：" + envelope.toString();
    }

    /**
     * 二倍均值法 拆分红包
     * 1. 判断是不是最后一个红包
     *    1.1 是，那么剩下的金额都是最后一个人的
     *    1.2 不是， 使用二倍均值法 进行红包拆分
     *        （ 0,剩下的金额 / 剩下的人数 * 2）
     *         红包拆分的金额不能为0
     * @param totalMoney
     * @param count
     * @return
     */
    private Integer[] splitEnvelopeAlgorithm(Integer totalMoney, Integer count) {
        Integer[] splitEnvelope = new Integer[count];
        Integer useMoney = 0;

        for (int i = 0; i < count; i++) {
            if (i == count -1){
                splitEnvelope[i] = totalMoney - useMoney;
            }else {
                Integer compute = (totalMoney - useMoney) / (count - i + 1);
                splitEnvelope[i] = 1 + new Random().nextInt(compute - 1);
                useMoney += splitEnvelope[i];
            }
        }

        return splitEnvelope;
    }

    /**
     * 抢红包功能
     * 1. 判断是够已经抢过了
     *    1.1 抢过了，返回error:-1 表示已经抢过 不能再抢啦~~~
     *    1.2 没有抢过
     *        1.2.1判断是否还有红包还能抢
     *          还有
     *          1.2.1.1 将list中的数据pop出来，比如res
     *          1.2.1.2 将userid res数据存入hash
     *        1.2.2 红包已经抢完了，返回error:-2 红包已经抢完了
     * @param userId
     * @return
     */
    public String hunt(String userId) {
        return null;
    }
}
