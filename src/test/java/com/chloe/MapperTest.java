package com.chloe;

import com.chloe.entity.Customer;
import com.chloe.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: MapperTest
 * Package: com.chloe
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 18:22
 * @Version 1.0
 */
@Slf4j
@SpringBootTest
public class MapperTest {
    @Autowired
    private CustomerMapper customerMapper;
    @Test
    public void test1(){
        // id回显
        Customer customer = new Customer();
        customer.setAge(19);
        customer.setCname("herry111");
        customer.setSex(Byte.valueOf("1"));
        customer.setPhone("13262816311");
        int rows = customerMapper.insertSelective(customer);
        log.info("插入数据： {}, 结果: {}",customer, rows);
    }
}
