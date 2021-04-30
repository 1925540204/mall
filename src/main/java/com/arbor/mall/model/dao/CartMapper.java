package com.arbor.mall.model.dao;

import com.arbor.mall.model.pojo.Cart;
import com.arbor.mall.model.vo.CartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    /**
     * 根据用户ID与商品ID查询购物车中是否有该商品
     * @param userId
     * @param productId
     * @return
     */
    Cart selectCartByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);


    /**
     * 查询购物车列表
     * @param userId
     * @return
     */
    List<CartVO> selectList(Integer userId);

    /**
     * 选中或不选中购物车中的商品
     * @param userId
     * @param productId
     * @param selected
     */
    void selectOrNot(@Param("userId") Integer userId, @Param("productId") Integer productId,
                     @Param("selected") Integer selected);
}