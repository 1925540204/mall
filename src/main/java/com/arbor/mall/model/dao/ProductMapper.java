package com.arbor.mall.model.dao;

import com.arbor.mall.model.pojo.Product;
import com.arbor.mall.model.query.ProductListQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * 根据名字查找商品信息，用于判断是否重名
     * @param name
     * @return
     */
    Product selectByName(String name);


    /**
     * 批量上下架商品
     * @return
     */
    int updateStatus(@Param("ids") Integer[] ids, @Param("sellStatus") Integer sellStatus);

    /**
     * 后台商品列表
     * @return
     */
    List<Product> selectListForAdmin();


    /**
     * 根据传入的规则查询商品列表
     * @param query
     * @return
     */
    List<Product> selectList(@Param("query") ProductListQuery query);
}