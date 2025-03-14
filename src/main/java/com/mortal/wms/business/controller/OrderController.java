package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.dto.OrderOutBoundPageRequest;
import com.mortal.wms.business.dto.OrderOutBoundRequest;
import com.mortal.wms.business.dto.OrderPageRequest;
import com.mortal.wms.business.dto.OrdersRequest;
import com.mortal.wms.business.entity.OrderReceipt;
import com.mortal.wms.business.service.OrderService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
@Slf4j
@Tag(name = "订单")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/homeData")
    @Operation(summary = "首页数据")
    @Transactional
    public ResultResponse homeData(Integer year) {
        return orderService.homeData(year);
    }

    @PostMapping("/add")
    @Operation(summary = "添加订单")
    @Transactional
    public ResultResponse addOrder(@CurrentUser UserVo userVo,@RequestBody OrdersRequest request) {
        return orderService.addOrder(userVo,request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    @Transactional
    public ResultResponse deleteOrder(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        return orderService.deleteOrder(userVo,id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询订单")
    public ResultResponse getOrderById(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        return orderService.getOrderById(userVo,id);
    }

    @GetMapping
    @Operation(summary = "查询所有订单")
    public ResultResponse listOrders(@CurrentUser UserVo userVo, OrderPageRequest request) {
        return orderService.listOrders(userVo,request);
    }

    //出库 影响 生产记录表  订单产品  这边需要从订单产品表记录一下成本
    @PostMapping("/outbound")
    @Operation(summary = "出库")
    @Transactional
    public ResultResponse outbound(@CurrentUser UserVo userVo, @RequestBody OrderOutBoundRequest request) {
        return orderService.outbound(userVo,request);
    }

    @PostMapping("/receipt")
    @Operation(summary = "订单收款")
    @Transactional
    public ResultResponse receipt(@CurrentUser UserVo userVo, @RequestBody OrderReceipt request) {
        return orderService.receipt(userVo,request);
    }

    @GetMapping("/outBoundRecord")
    @Operation(summary = "出库记录")
    @Transactional
    public ResultResponse outBoundRecord(@CurrentUser UserVo userVo,OrderOutBoundPageRequest request) {
        return orderService.outBoundRecordlist(userVo,request);
    }
}