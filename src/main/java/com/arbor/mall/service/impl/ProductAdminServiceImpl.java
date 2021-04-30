package com.arbor.mall.service.impl;


import com.arbor.mall.common.Constant;
import com.arbor.mall.exception.ArborMallException;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.dao.ProductMapper;
import com.arbor.mall.model.pojo.Category;
import com.arbor.mall.model.pojo.Product;
import com.arbor.mall.model.query.ProductListQuery;
import com.arbor.mall.model.request.AddProductReq;
import com.arbor.mall.model.request.ProductListReq;
import com.arbor.mall.model.request.UpdateProductReq;
import com.arbor.mall.model.vo.CategoryVO;
import com.arbor.mall.service.CategoryService;
import com.arbor.mall.service.ProductAdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * 描述：后台商品管理的Service实现类
 */
@Service
public class ProductAdminServiceImpl implements ProductAdminService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;

    /**
     * 添加商品
     *
     * @param addProductReq
     */
    @Override
    public void add(AddProductReq addProductReq) {

        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null) {
            throw new ArborMallException(ArborMallExceptionEnum.NAME_EXISTED);
        }

        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.CREATE_FAILED);
        }

    }

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @Override
    public String upload(MultipartFile file) {
        // 获取传入的文件名的后缀
        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf("."));
        // 生成UUID文件名
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        // 创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        // 判断目录是否存在，存在则跳出if
        if (!fileDirectory.exists()) {
            // 不存在，则创建文件夹，创建成功则跳出if
            if (!fileDirectory.mkdir()) {
                // 创建失败，抛出异常
                throw new ArborMallException(ArborMallExceptionEnum.MKDIR_FAILED);
            }
        }
        // 将上传的图片保存到新的file中
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newFileName;
    }

    /**
     * 更新商品信息
     *
     * @param updateProductReq
     */
    @Override
    public void update(UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        Product productOld = productMapper.selectByName(product.getName());
        if (productOld != null && !productOld.getId().equals(product.getId())) {
            throw new ArborMallException(ArborMallExceptionEnum.NAME_EXISTED);
        }

        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.UPDATE_FAILED);
        }

    }

    /**
     * 删除一个商品
     *
     * @param productId
     */
    @Override
    public void delete(Integer productId) {

        Product productOld = productMapper.selectByPrimaryKey(productId);
        if (productOld == null) {
            throw new ArborMallException(ArborMallExceptionEnum.DELETE_FAILED);
        }

        int count = productMapper.deleteByPrimaryKey(productId);
        if (count == 0) {
            throw new ArborMallException(ArborMallExceptionEnum.DELETE_FAILED);
        }
    }

    /**
     * 商品批量上下架
     *
     * @param ids
     * @param sellStatus
     */
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        productMapper.updateStatus(ids, sellStatus);
    }

    /**
     * 后台商品列表
     *
     * @param pageSize
     * @param pageNum
     */
    @Override
    public PageInfo listForAdmin(Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    /**
     * 查询一个商品
     *
     * @param id
     * @return
     */
    @Override
    public Product detail(Integer id) {
        return productMapper.selectByPrimaryKey(id);
    }

    /**
     * 前台商品列表
     *
     * @param productListReq
     * @return
     */
    @Override
    public PageInfo list(ProductListReq productListReq) {

        ProductListQuery productListQuery = new ProductListQuery();

        // 搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder()
                    .append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        // 目录处理，如果查询的分类里面有子分类，则需要将子分类中的商品一同查出
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOList =
                    categoryService.listCategoryForCustomer(productListReq.getCategoryId());

            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        // 排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        }else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }

        List<Product> products = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }


    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }


}
