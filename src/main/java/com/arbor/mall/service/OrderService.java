package com.arbor.mall.service;

import com.arbor.mall.model.request.CreateOrderReq;
import com.arbor.mall.model.vo.OrderVO;
import com.github.pagehelper.PageInfo;

/**
 * 描述：订单service
 */
public interface OrderService {


    /**
     * 创建订单
     * @param userId
     * @param createOrderReq
     * @return
     */
    String create(Integer userId, CreateOrderReq createOrderReq);


    /**
     * 订单详情
     * @param userId
     * @param orderNo
     * @return
     */
    OrderVO detail(Integer userId, String orderNo);


    /**
     * 前台订单列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo listForCustomer(Integer userId, Integer pageNum, Integer pageSize);


    /**
     * 取消订单
     * @param userId
     * @param orderNo
     */
    void cancel(Integer userId,String orderNo);

    /**
     * 生成支付二维码
     * @param orderNo
     * @return
     */
    String qrcode( String orderNo);


    /**
     * 后台订单列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo listForAdmin(Integer pageNum, Integer pageSize);

    /**
     * 支付订单
     * @param orderNo
     */
    void pay(String orderNo);


    /**
     * 后台订单发货
     * @param orderNo
     */
    void delivered(String orderNo);

    /**
     * 完结订单
     *
     * @param orderNo
     */
    void finish(String orderNo);
}
