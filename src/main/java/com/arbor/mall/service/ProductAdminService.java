package com.arbor.mall.service;

import com.arbor.mall.model.pojo.Product;
import com.arbor.mall.model.request.AddProductReq;
import com.arbor.mall.model.request.ProductListReq;
import com.arbor.mall.model.request.UpdateProductReq;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 描述：后台商品管理的Service
 */
public interface ProductAdminService {

    /**
     * 添加商品
     * @param addProductReq
     */
    void add(AddProductReq addProductReq);

    /**
     * 上传图片
     * @param file
     * @return
     */
    String upload(MultipartFile file);


    /**
     * 更新商品信息
     * @param updateProductReq
     */
    void update(UpdateProductReq updateProductReq);


    /**
     * 删除一个商品
     * @param productId
     */
    void delete(Integer productId);


    /**
     * 商品批量上下架
     * @param ids
     * @param sellStatus
     */
    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    /**
     * 后台商品列表
     * @param pageSize
     * @param pageNum
     */
    PageInfo listForAdmin(Integer pageSize, Integer pageNum);

    /**
     * 查询一个商品
     * @param id
     * @return
     */
    Product detail(Integer id);


    /**
     * 前台商品列表
     * @param productListReq
     * @return
     */
    PageInfo list(ProductListReq productListReq);
}
