package com.zysl.consum.service;

import com.zysl.consum.dao.Product;


public interface ConsumService {

    Product queryProductById(Long id);

    int updateProductAmount(Integer amount, Long id);

    int updateOrder(Integer money, Long id);

    int updatePay();
}
