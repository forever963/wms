package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.dto.SupplierInfoPageRequest;
import com.mortal.wms.business.entity.SupplierInfo;
import com.mortal.wms.business.service.SupplierInfoService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/supplierInfo")
@Tag(name = "供应商", description = "供应商接口")
public class SupplierInfoController {
    @Autowired
    private SupplierInfoService supplierInfoService;

    @Operation(summary = "新增")
    @PostMapping("/add")
    public ResultResponse add(@CurrentUser UserVo userVo, @RequestBody SupplierInfo supplierInfo) {
        ResultResponse response = supplierInfoService.add(userVo, supplierInfo);
        return response;
    }

    @Operation(summary = "删除")
    @GetMapping("/delete/{id}")
    public ResultResponse add(@CurrentUser UserVo userVo, @PathVariable Integer id) {
        ResultResponse response = supplierInfoService.delete(userVo, id);
        return response;
    }

    @Operation(summary = "列表")
    @GetMapping("/list")
    public ResultResponse list(@CurrentUser UserVo userVo, SupplierInfoPageRequest request) {
        ResultResponse response =supplierInfoService.list(userVo,request);
        return response;
    }

    @Operation(summary = "详情")
    @GetMapping("/detail/{id}")
    public ResultResponse detail(@CurrentUser UserVo userVo, @PathVariable Integer id) {
        ResultResponse response = supplierInfoService.detail(userVo, id);
        return response;
    }

    @Operation(summary = "编辑")
    @PutMapping("/update")
    public ResultResponse update(@CurrentUser UserVo userVo, @RequestBody SupplierInfo supplierInfo) {
        ResultResponse response = supplierInfoService.update(userVo,supplierInfo);
        return response;
    }
}
