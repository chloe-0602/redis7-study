package com.chloe;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * ClassName: CardinalStatisticsTest
 * Package: com.chloe
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 10:34
 * @Version 1.0
 */
@SpringBootTest
@Slf4j
public class CardinalStatisticsTest {
    @Test
    public void test1() {
        List<String> list = Arrays.asList("192.168.11.11", "127.0.0.1", "192.168.11.11", "12.2.1.0");
        HashSet<String> set = new HashSet<>(list);
        System.out.println(set);
    }

    @Test
    public void test2() {
        // 0.008125% -> 也就是说 99.18%的准确率
        log.info("HyperLogLog的误差统计： {}", 1.04 / Math.sqrt(16384));
    }

    @Test
    public void test3() {
        Integer res = 16384 * 6 / 8;
        log.info("计算HyperLogLog占用的内存空间： {}", res);
        log.info("计算HyperLogLog占用的内存空间： {}KB", res/1024);
    }
}
