package com.chloe;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: GuavaBloomTest
 * Package: com.chloe
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 20:54
 * @Version 1.0
 */
@Slf4j
@SpringBootTest
public class GuavaBloomTest {
    @Test
    public void test1(){
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), 100);
        log.info("第一次判断 => 1是否存在：{}",  bloomFilter.mightContain(1));
        log.info("第一次判断 => 2是否存在：{}",  bloomFilter.mightContain(2));

        log.info("-------------");

        bloomFilter.put(1);
        bloomFilter.put(2);

        log.info("第二次判断 => 1是否存在：{}",  bloomFilter.mightContain(1));
        log.info("第二次判断 => 2是否存在：{}",  bloomFilter.mightContain(2));
    }
}
