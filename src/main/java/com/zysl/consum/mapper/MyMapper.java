package com.zysl.consum.mapper;

import org.apache.ibatis.annotations.Param;

public interface MyMapper {

    int updateProductAmount(@Param("amount") Integer amount, @Param("id") Long id);

    int updateOrderMony(@Param("money") Integer money, @Param("id") Long id);

}
