package com.arbor.mall.controller;

import com.arbor.mall.common.ApiRestResponse;
import com.arbor.mall.filter.UserFilter;
import com.arbor.mall.model.request.CreateOrderReq;
import com.arbor.mall.model.vo.OrderVO;
import com.arbor.mall.service.OrderService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Filter;

/**
 * 描述：订单Controller
 */
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;


    @ApiOperation("创建订单")
    @PostMapping("/order/create")
    public ApiRestResponse create(@Valid @RequestBody CreateOrderReq createOrderReq){
        String orderNo = orderService.create(UserFilter.currentUser.getId(), createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @ApiOperation("订单详情")
    @GetMapping("/order/detail")
    public ApiRestResponse detail(@RequestParam String orderNo){
        OrderVO orderVO = orderService.detail(UserFilter.currentUser.getId(), orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @ApiOperation("前台订单列表")
    @GetMapping("/order/list")
    public ApiRestResponse listForCustomer(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        PageInfo pageInfo = orderService.listForCustomer(UserFilter.currentUser.getId(), pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("取消订单")
    @PostMapping("/order/cancel")
    public ApiRestResponse cancel(@RequestParam String orderNo){
        orderService.cancel(UserFilter.currentUser.getId(), orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("生成支付二维码")
    @GetMapping("/order/qrcode")
    public ApiRestResponse qrcode(@RequestParam String orderNo){
        String qrcode = orderService.qrcode(orderNo);
        return ApiRestResponse.success(qrcode);
    }

    @ApiOperation("支付订单")
    @GetMapping("/pay")
    public ApiRestResponse pay(@RequestParam String orderNo){
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }


}
