package com.arbor.mall.service;

import com.arbor.mall.model.vo.CartVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 描述：购物车Service
 */
public interface CartService {


    /**
     * 购物车列表
     * @param userId
     * @return
     */
    List<CartVO> list(Integer userId);

    /**
     * 添加商品到购物车
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    /**
     * 更新购物车商品数量
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    List<CartVO> update(Integer userId, Integer productId, Integer count);


    /**
     * 删除购物车中某一个商品
     * @param userId
     * @param productId
     * @return
     */
    List<CartVO> delete(Integer userId, Integer productId);


    /**
     * 选中/不选中购物车商品
     * @param userId
     * @param productId
     * @param selected
     * @return
     */
    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    /**
     * 全选中/全不选中购物车商品
     *
     * @param userId
     * @param selected
     * @return
     */
    List<CartVO> selectAllOrNot(Integer userId, Integer selected);
}
