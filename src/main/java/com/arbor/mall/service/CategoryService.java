package com.arbor.mall.service;

import com.arbor.mall.model.pojo.Category;
import com.arbor.mall.model.request.AddCategoryReq;
import com.arbor.mall.model.request.UpdateCategoryReq;
import com.arbor.mall.model.vo.CategoryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 描述：目录，商品分类Service
 */
public interface CategoryService {


    /**
     * 添加商品分类信息
     * @param addCategoryReq
     */
    void addCategory(AddCategoryReq addCategoryReq);


    /**
     * 更新商品分类信息
     * @param updateCategory
     */
    void updateCategory(Category updateCategory);


    /**
     * 删除商品分类
     * @param id
     */
    void deleteCategory(Integer id);


    /**
     * 后台分类列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize);


    /**
     * 前台分类列表
     * @return
     */
    List<CategoryVO> listCategoryForCustomer(Integer parentId);

}
