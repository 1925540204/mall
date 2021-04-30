package com.arbor.mall.model.dao;

import com.arbor.mall.model.pojo.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);


    /**
     * 根据订单号查询订单信息
     * @param orderNo
     * @return
     */
    Order selectByOrderNo(String orderNo);


    /**
     * 根据用户ID查询订单列表
     * @param userId
     * @return
     */
    List<Order> selectByUserId(Integer userId);


    /**
     * 查询所有订单
     * @return
     */
    List<Order> selectOrderList();

}