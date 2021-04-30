package com.arbor.mall.model.dao;

import com.arbor.mall.model.pojo.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);


    /**
     * 根据订单号查询用户子订单集合
     * @param orderNo
     * @return
     */
    List<OrderItem> selectOrderItemList(String orderNo);
}