package com.arbor.mall.controller;

import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：后台订单Controller
 */
@RestController
public class OrderAdminController {

    @Autowired
    OrderService orderService;


    @ApiOperation("后台订单列表")
    @GetMapping("/admin/order/list")
    public ApiRestResponse listForAdmin(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("后台订单发货")
    @PostMapping("/admin/order/delivered")
    public ApiRestResponse delivered(@RequestParam String orderNo){
        orderService.delivered(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("订单完结")
    @PostMapping("/order/finish")
    public ApiRestResponse finish(@RequestParam String orderNo){
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }


}
