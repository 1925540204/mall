package com.arbor.mall.controller;

import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.common.Constant;
import com.arbor.mall.exception.ArborMallExceptionEnum;
import com.arbor.mall.model.pojo.Category;
import com.arbor.mall.model.pojo.User;
import com.arbor.mall.model.request.AddCategoryReq;
import com.arbor.mall.model.request.UpdateCategoryReq;
import com.arbor.mall.model.vo.CategoryVO;
import com.arbor.mall.service.CategoryService;
import com.arbor.mall.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

/**
 * 描述：目录，商品分类Controller
 */

@Controller
public class CategoryController {

    @Autowired
    UserService userService;
    @Autowired
    CategoryService categoryService;


    /**
     * 后台添加分类
     * @param session
     * @param addCategoryReq
     * @return
     */
    @ApiOperation("添加后台分类")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session,
                                       @Valid @RequestBody AddCategoryReq addCategoryReq){

        User currentUser = (User)session.getAttribute(Constant.ARBOR_MALL_USER);
        // 判断用户是否登录
        if (currentUser == null){
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_LOGIN);
        }
        // 判断是否有管理员权限
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole){
            // 有管理员权限的话，执行此操作
            categoryService.addCategory(addCategoryReq);
            return ApiRestResponse.success();
        }else {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("更新后台分类")
    @PostMapping("/admin/category/update")
    @ResponseBody
    public ApiRestResponse updateCategory(HttpSession session,
                                          @Valid @RequestBody UpdateCategoryReq updateCategoryReq){
        User currentUser = (User)session.getAttribute(Constant.ARBOR_MALL_USER);
        // 判断用户是否登录
        if (currentUser == null){
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_LOGIN);
        }
        // 判断是否有管理员权限
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole){
            // 有管理员权限的话，执行此操作
            Category category = new Category();
            BeanUtils.copyProperties(updateCategoryReq, category);
            categoryService.updateCategory(category);
            return ApiRestResponse.success();
        }else {
            return ApiRestResponse.error(ArborMallExceptionEnum.NEED_ADMIN);
        }
    }

    @ApiOperation("删除后台分类")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.deleteCategory(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台分类列表")
    @GetMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,
                                                @RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listCategoryForAdmin(pageNum, pageSize);

        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("前台分类列表")
    @GetMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOList = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOList);
    }


}
