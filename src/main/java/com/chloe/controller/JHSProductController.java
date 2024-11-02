package com.chloe.controller;

import com.chloe.entity.Product;
import com.chloe.service.JHSTaskService;
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
    private JHSTaskService jhsTaskService;

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
        return jhsTaskService.find(page, size);
    }
}
