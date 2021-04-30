package com.arbor.mall.controller;

import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.model.pojo.Product;
import com.arbor.mall.model.request.ProductListReq;
import com.arbor.mall.service.ProductAdminService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 描述：前台商品controller
 */
@Controller
public class ProductController {

    @Autowired
    ProductAdminService productAdminService;

    @ApiOperation("前台商品详情")
    @GetMapping("/product/detail")
    @ResponseBody
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product product = productAdminService.detail(id);
        return ApiRestResponse.success(product);
    }

    @ApiOperation("前台商品列表")
    @GetMapping("/product/list")
    @ResponseBody
    public ApiRestResponse list(ProductListReq productListReq) {
        PageInfo pageInfo = productAdminService.list(productListReq);
        return ApiRestResponse.success(pageInfo);
    }




}
