package com.zysl.consum.service;

import com.zysl.consum.dao.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan({"com.zysl.aws.mapper","com.zysl.consum.mapper"})
public class ConsumServiceTest {

    @Autowired
    private ConsumService consumService;

    @Test
    public void queryProductById(){
        Product product = consumService.queryProductById(1L);
        System.out.println(product);
    }

    @Test
    public void updateProductAmount(){
        int num = consumService.updateProductAmount(1, 1L);
        System.out.println(num);
    }

    @Test
    public void updateOrder(){
        int num = consumService.updateOrder(99, 1L);
        System.out.println(num);
    }

    @Test
    public void updatePay(){
        consumService.updatePay();
        System.out.println("----");
    }

}