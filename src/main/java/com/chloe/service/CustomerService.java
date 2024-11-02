package com.chloe.service;

import com.chloe.entity.Customer;
import com.chloe.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ClassName: CustomerService
 * Package: com.chloe.service
 * Description:
 *
 * @Author Xu, Luqin
 * @Create 2024/11/2 18:39
 * @Version 1.0
 */
@Service
@Slf4j
public class CustomerService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    CustomerMapper customerMapper;
    private final static String CACHE_CUSTOMER_KEY = "customer:";


    /**
     * 添加客户
     * 1.先添加到MySQL
     * 2.回写Redis
     *
     * @param customer
     */
    public void addCustomer(Customer customer) {
        int rows = customerMapper.insertSelective(customer);
        if (rows > 0) {

            log.info("在MySQL中插入数据： {}", customer);

            String key = CACHE_CUSTOMER_KEY + customer.getId();
            redisTemplate.opsForValue().set(key, customer);

            log.info("在Redis中回写数据：{} => {}", key, customer);
        }
    }

    /**
     * 根据id查询用户信息
     * 1.先查询Redis缓存
     *  2.如果没有查询到数据， 则查询MySQL数据库
     *    3.如果在MySQL查询到数据，则将查询到的数据回写都Redis缓存中
     * @param id
     * @return
     */
    public Customer findCustomerById(int id) {
        Customer customer = null;

        String key = CACHE_CUSTOMER_KEY + id;
        customer = (Customer) redisTemplate.opsForValue().get(key);

        if(customer == null){
            customer = customerMapper.selectByPrimaryKey(id);

            if (customer != null){
                redisTemplate.opsForValue().set(key, customer);
            }

        }
        return customer;
    }
}
