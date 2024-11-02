package com.chloe;

import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

/**
 * ClassName: HashTest
 * Package: com.chloe
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 14:16
 * @Version 1.0
 */
@SpringBootTest
@Slf4j
public class HashTest {
    /**
     * 模拟Hash冲突
     */
    @Test
    public void testCollision(){
        System.out.println("Aa".hashCode());
        System.out.println("BB".hashCode());
    }

    @Test
    public void testCollision1(){
        System.out.println("柳柴".hashCode());
        System.out.println("柴柕".hashCode());
    }


    @Test
    public void testCollision3(){
        HashSet<Integer> set = new HashSet<>();
        int hashCode;
        for (int i = 0; i < 200000; i++) {
            hashCode = new Object().hashCode();
            if (set.contains(hashCode)){
                log.info("运行到第{}次，出现了Hash冲突，hashcode：{}", i, hashCode);
            }
            set.add(hashCode);
        }
    }
}
