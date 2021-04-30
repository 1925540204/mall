package com.arbor.mall.service.impl;

import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.dao.CartMapper;
import com.arbor.mall.model.dao.CategoryMapper;
import com.arbor.mall.model.pojo.Category;
import com.arbor.mall.model.request.AddCategoryReq;
import com.arbor.mall.model.request.UpdateCategoryReq;
import com.arbor.mall.model.vo.CategoryVO;
import com.arbor.mall.service.CategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：目录，商品分类Service实现类
 */
@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 添加商品目录
     *
     * @param addCategoryReq
     */
    @Override
    public void addCategory(AddCategoryReq addCategoryReq) {

        // 将传参放入实体类中
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);

        // 校验传入的类目名是否同名
        Category categoryOld = categoryMapper.selectName(category.getName());
        if (categoryOld != null) {
            throw new ArborMallException(ArborMallExceptionEnum.NAME_EXISTED);
        }

        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.CREATE_FAILED);
        }


    }


    /**
     * 更新商品分类信息
     *
     * @param updateCategory
     */
    @Override
    public void updateCategory(Category updateCategory) {

        // 校验传入的类目名是否同名
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selectName(updateCategory.getName());

            // 如果查询到的不是空的并且传入的id和查询到的id不一样的话，则抛出异常
            if (categoryOld != null && !updateCategory.getId().equals(categoryOld.getId())) {
                throw new ArborMallException(ArborMallExceptionEnum.NAME_EXISTED);
            }
        }

        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 删除商品分类
     *
     * @param id
     */
    @Override
    public void deleteCategory(Integer id) {

        // 数据库查不到记录，删除失败
        Category categoryOld = categoryMapper.selectByPrimaryKey(id);
        if (categoryOld == null) {
            throw new ArborMallException(ArborMallExceptionEnum.DELETE_FAILED);
        }


        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.DELETE_FAILED);
        }

    }

    /**
     * 后台分类列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo listCategoryForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categoryList = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categoryList);
        return pageInfo;
    }

    /**
     * 前台分类列表
     *
     * @return
     */
    @Override
    @Cacheable(value = "listCategoryForCustomer")
    public List<CategoryVO> listCategoryForCustomer(Integer parentId) {
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategories(categoryVOList, parentId);
        return categoryVOList;
    }

    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId) {
        // 通过递归获取所有子类别，并组合成一个目录树
        // 先通过父id查询出categoryList
        List<Category> categoryList = categoryMapper.selectCategoryByParentId(parentId);
        // 如果categoryList不是空的或者里面有值
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                // 将查询到的category转换成categoryVO
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                // 将转换的categoryVO添加到父目录的categoryVOList中
                categoryVOList.add(categoryVO);
                // 执行下一次查询
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
