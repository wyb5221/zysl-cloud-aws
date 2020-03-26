package com.zysl.consum.service.impl;

import com.zysl.consum.dao.OrderInfo;
import com.zysl.consum.dao.Product;
import com.zysl.consum.mapper.MyMapper;
import com.zysl.consum.mapper.OrderInfoMapper;
import com.zysl.consum.mapper.ProductMapper;
import com.zysl.consum.service.ConsumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsumServiceImpl implements ConsumService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private MyMapper myMapper;


    @Override
    public Product queryProductById(Long id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    /*REQUIRED(0),支持使用当前事务，如果当前事务不存在，创建一个新事务。
    SUPPORTS(1),支持使用当前事务，如果当前事务不存在，则不使用事务。 嵌套时先执行方法无事务
    MANDATORY(2),支持使用当前事务，如果当前事务不存在，则抛出Exception
    REQUIRES_NEW(3),创建一个新事务，如果当前事务存在，把当前事务挂起。
    NOT_SUPPORTED(4),无事务执行，如果当前事务存在，把当前事务挂起。 嵌套时先执行方法无事务
    NEVER(5),无事务执行，如果当前有事务则抛出Exception。嵌套时先执行方法无事务
    NESTED(6);嵌套事务，如果当前事务存在，那么在嵌套的事务中执行。如果当前事务不存在，则表现跟REQUIRED一样。
    */
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public int updateProductAmount(Integer amount, Long id) {
        int num = myMapper.updateProductAmount(amount, id);
            System.out.println(1/0);
        return num;
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public int updateOrder(Integer money, Long id) {
        updateProductAmount(1,1L);

        int num = myMapper.updateOrderMony(1,1L);
            System.out.println(1/0);
        return num;
    }

    @Override
    public int updatePay() {

        updateOrder(98, 1L);
        return 0;
    }


}
