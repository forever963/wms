package com.mortal.wms.business.controller;

import com.mortal.wms.business.entity.CustomerInfo;
import com.mortal.wms.business.service.CustomerInfoService;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customerInfo")
@Tag(name = "客户管理")
public class CustomerInfoController {
    @Autowired
    private CustomerInfoService customerService;

    @PostMapping
    @Operation(summary = "添加客户")
    public ResultResponse addCustomer(@RequestBody CustomerInfo customer) {
        return customerService.addCustomer(customer);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户")
    public ResultResponse deleteCustomer(@PathVariable Integer id) {
        return customerService.deleteCustomer(id);
    }

    @PutMapping
    @Operation(summary = "更新客户信息")
    public ResultResponse updateCustomer(@RequestBody CustomerInfo customer) {
        return customerService.updateCustomer(customer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询客户")
    public ResultResponse getCustomerById(@PathVariable Integer id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    @Operation(summary = "查询所有客户")
    public ResultResponse listCustomers(PageRequest request) {
        return customerService.listCustomers(request);
    }
}
