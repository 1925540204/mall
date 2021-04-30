package com.arbor.mall.model.dao;

import com.arbor.mall.model.pojo.Category;
import com.arbor.mall.model.vo.CategoryVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);


    /**
     * 根据传入的分类名查找数据库是否存在
     * @param name
     * @return
     */
    Category selectName(String name);


    /**
     * 查询所有，并返回一个List集合
     * @return
     */
    List<Category> selectList();

    /**
     * 根据父分类id查询子分类
     * @return
     */
    List<Category> selectCategoryByParentId(Integer parentId);
}