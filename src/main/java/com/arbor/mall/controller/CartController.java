package com.arbor.mall.controller;

import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.filter.UserFilter;
import com.arbor.mall.model.vo.CartVO;
import com.arbor.mall.service.CartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：购物车controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @ApiOperation("购物车列表")
    @GetMapping("/list")
    public ApiRestResponse list(){
        List<CartVO> list = cartService.list(UserFilter.currentUser.getId());
        return ApiRestResponse.success(list);
    }


    @ApiOperation("添加到购物车")
    @PostMapping("/add")
    public ApiRestResponse add(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> cartVOS = cartService.add(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("更新购物车商品数量")
    @PostMapping("/update")
    public ApiRestResponse update(@RequestParam Integer productId, @RequestParam Integer count){
        List<CartVO> cartVOS = cartService.update(UserFilter.currentUser.getId(), productId, count);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("删除购物车商品")
    @PostMapping("/delete")
    public ApiRestResponse delete(@RequestParam Integer productId){ // 不传cartId，对信息更安全
        List<CartVO> cartVOS = cartService.delete(UserFilter.currentUser.getId(), productId);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("选中/不选中购物车商品")
    @PostMapping("/select")
    public ApiRestResponse select(@RequestParam Integer productId ,@RequestParam Integer selected){
        List<CartVO> cartVOS = cartService.selectOrNot(UserFilter.currentUser.getId(), productId, selected);
        return ApiRestResponse.success(cartVOS);
    }

    @ApiOperation("全选中/全不选中购物车商品")
    @PostMapping("/selectAll")
    public ApiRestResponse selectAll(@RequestParam Integer selected){
        List<CartVO> cartVOS = cartService.selectAllOrNot(UserFilter.currentUser.getId(), selected);
        return ApiRestResponse.success(cartVOS);
    }
}
