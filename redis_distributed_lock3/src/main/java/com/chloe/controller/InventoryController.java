package com.chloe.controller;

import com.chloe.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: InventoryController
 * Package: com.chloe.controller
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/3 9:56
 * @Version 1.0
 */
@RestController
@Api(tags = "redis分布式锁测试")
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @ApiOperation("扣减库存，一次卖一个")
    @GetMapping(value = "/sale")
    public String sale() {
        return inventoryService.sale();
    }
}