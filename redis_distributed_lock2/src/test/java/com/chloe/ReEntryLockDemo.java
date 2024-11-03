package com.chloe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: ReEntryLockDemo
 * Package: com.chloe
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/3 13:54
 * @Version 1.0
 */

public class ReEntryLockDemo {
    public static void main(String[] args) {

    }

    public void test1(){
        Object obj = new Object();
        new Thread(() -> {
            synchronized (obj){
                System.out.println();
            }
        },"t1").start();
    }
}
