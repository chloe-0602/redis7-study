package com.chloe.controller;

import com.chloe.service.HyberLogLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: HyberLogLogController
 * Package: com.chloe.controller
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 11:30
 * @Version 1.0
 */
@Api("HyberLogLog接口")
@RestController
@RequestMapping("hll")
public class HyberLogLogController {
    @Autowired
    private HyberLogLogService hyberLogLogService;
    @ApiOperation("获取不重复的UV数据")
    @GetMapping("uv")
    public Long getUv(){
        return hyberLogLogService.getUv();
    }
}
