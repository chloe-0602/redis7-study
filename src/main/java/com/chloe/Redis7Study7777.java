package com.chloe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.chloe.mapper")
public class Redis7Study7777 {
    public static void main(String[] args) {
        SpringApplication.run(Redis7Study7777.class,args);
    }
}


