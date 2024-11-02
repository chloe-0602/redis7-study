package com.chloe.controller;

import com.chloe.entity.Product;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: JHSProductController
 * Package: com.chloe.controller
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/3 6:54
 * @Version 1.0
 */
@Api(tags = "聚划算商品列表接口")
@Slf4j
@RestController
@RequestMapping("/product")
public class JHSProductController {

    @Autowired
    private RedisTemplate redisTemplate;

    private final static String JHS_KEY = "jhs";

    /**
     * 高并发 + 定时任务 + 分页任务
     * 不能直接使用MySQL， 会被打爆！
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/find")
    public List<Product> find(int page, int size) {
        List<Product> list = null;

        int start = (page - 1) * size;
        int end = start + size;

        try {
            list = redisTemplate.opsForList().range(JHS_KEY, start, end);
            if (list == null) {
                /**
                 * 隐患2： 当缓存查询不到时，会直接打到MySQL
                 */
                // TODO 走 DB
            }
        } catch (Exception e) {
            log.error("查询不到缓存，错误是： {}", e);
            e.printStackTrace();
            // TODO 走 DB 查询
        }

        return list;
    }
}
