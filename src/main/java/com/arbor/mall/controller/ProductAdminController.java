package com.arbor.mall.controller;

import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.request.AddProductReq;
import com.arbor.mall.model.request.UpdateProductReq;
import com.arbor.mall.service.ProductAdminService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 描述：后台商品管理的Controller
 */
@Controller
public class ProductAdminController {

    @Autowired
    ProductAdminService productAdminService;

    /**
     * 添加商品
     *
     * @param addProductReq
     * @return
     */
    @ApiOperation("后台添加商品")
    @PostMapping("/admin/product/add")
    @ResponseBody
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productAdminService.add(addProductReq);
        return ApiRestResponse.success();
    }


    /**
     * 图片上传
     *
     * @param request
     * @param file
     * @return
     */
    @ApiOperation("图片上传")
    @PostMapping("admin/upload/file")
    @ResponseBody
    public ApiRestResponse upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {

        String newFileName = productAdminService.upload(file);
        try {
            return ApiRestResponse.success(getHost(new URI(request.getRequestURL() + ""))
                    + "/images/" + newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ArborMallExceptionEnum.UPLOAD_FAILED);
        }

    }

    /**
     * 过滤传入的URI
     *
     * @param uri
     * @return
     */
    private URI getHost(URI uri) {
        URI effectiveURI;

        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(),
                    uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }


    /**
     * 更新商品信息
     * @param updateProductReq
     * @return
     */
    @ApiOperation("后台更新商品")
    @PostMapping("admin/product/update")
    @ResponseBody
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq){
        productAdminService.update(updateProductReq);
        return ApiRestResponse.success();
    }


    /**
     * 删除一个商品
     * @param id
     * @return
     */
    @ApiOperation("后台删除商品")
    @PostMapping("admin/product/delete")
    @ResponseBody
    public ApiRestResponse deleteProduct(@RequestParam Integer id){
        productAdminService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("商品批量上下架")
    @PostMapping("admin/product/batchUpdateSellStatus")
    @ResponseBody
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                 @RequestParam Integer sellStatus){
        productAdminService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表")
    @GetMapping("admin/product/list")
    @ResponseBody
    public ApiRestResponse listProductForAdmin(@RequestParam Integer pageSize,
                                                 @RequestParam Integer pageNum){
        PageInfo pageInfo = productAdminService.listForAdmin(pageSize, pageNum);
        return ApiRestResponse.success(pageInfo);
    }



}
