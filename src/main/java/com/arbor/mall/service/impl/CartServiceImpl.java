package com.arbor.mall.service.impl;

import com.arbor.mall.common.Constant;
import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.dao.CartMapper;
import com.arbor.mall.model.dao.ProductMapper;
import com.arbor.mall.model.pojo.Cart;
import com.arbor.mall.model.pojo.Product;
import com.arbor.mall.model.vo.CartVO;
import com.arbor.mall.service.CartService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;


    /**
     * 购物车列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartVO> list(Integer userId) {

        List<CartVO> cartVOList = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOList) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOList;
    }


    /**
     * 添加商品到购物车
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {

        validProduct(productId, count);

        // 判断购物车中是否已存在该商品
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 如果购物车中没有记录，则保存该条数据
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.CHECKED);
            cartMapper.insertSelective(cart);
        } else {
            // 如果购物车中有此条记录，则添加数量，并更新
            count = cart.getQuantity() + count;
            Cart cartNew = new Cart();
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setQuantity(count);
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }


        return this.list(userId);
    }


    private void validProduct(Integer productId, Integer count) {
        // 判断商品是否存在并且在售，不存在或者库存不足则抛出异常
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new ArborMallException(ArborMallExceptionEnum.NOT_SALE);
        }
        if (count > product.getStock()) {
            throw new ArborMallException(ArborMallExceptionEnum.NOT_ENOUGH);
        }
    }


    /**
     * 更新购物车商品数量
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {

        validProduct(productId, count);

        // 判断购物车中是否已存在该商品
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 如果购物车中没有记录，则无法更新
            throw new ArborMallException(ArborMallExceptionEnum.UPDATE_FAILED);
        } else {
            // 如果购物车中有此条记录，则添加数量，并更新
            Cart cartNew = new Cart();
            cartNew.setId(cart.getId());
            cartNew.setProductId(cart.getProductId());
            cartNew.setUserId(cart.getUserId());
            cartNew.setQuantity(count);
            cartNew.setSelected(Constant.Cart.CHECKED);
            cartMapper.updateByPrimaryKeySelective(cartNew);
        }
        return this.list(userId);
    }

    /**
     * 删除购物车中某一个商品
     *
     * @param userId
     * @param productId
     * @return
     */
    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new ArborMallException(ArborMallExceptionEnum.DELETE_FAILED);
        } else {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    /**
     * 选中/不选中购物车商品
     *
     * @param userId
     * @param productId
     * @param selected
     * @return
     */
    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new ArborMallException(ArborMallExceptionEnum.UPDATE_FAILED);
        } else {
            cartMapper.selectOrNot(userId, productId, selected);
        }
        return this.list(userId);
    }

    /**
     * 全选中/全不选中购物车商品
     *
     * @param userId
     * @param selected
     * @return
     */
    @Override
    public List<CartVO> selectAllOrNot(Integer userId, Integer selected) {
        cartMapper.selectOrNot(userId, null, selected);
        return this.list(userId);
    }
}
