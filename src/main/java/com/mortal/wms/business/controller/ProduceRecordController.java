package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.dto.ProduceRecordRequest;
import com.mortal.wms.business.dto.ProduceMaterialPageRequest;
import com.mortal.wms.business.dto.ProductPageRequest;
import com.mortal.wms.business.service.ProduceRecordService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("productRecord")
@Tag(name = "生产")
public class ProduceRecordController {
    @Autowired
    private ProduceRecordService produceRecordService;

    @PostMapping("add")
    @Operation(summary = "新增(生产)")
    @Transactional
    public ResultResponse add(@CurrentUser UserVo userVo, @RequestBody ProduceRecordRequest request) {
        ResultResponse response = produceRecordService.add(userVo,request);
        return response;
    }

    @GetMapping("/produceRecordList")
    @Operation(summary = "生产记录列表")
    public ResultResponse ProduceRecordList(@CurrentUser UserVo userVo, ProductPageRequest request) {
        ResultResponse response = produceRecordService.produceRecordList(userVo,request);
        return response;
    }

    @GetMapping("/produceMaterialList")
    @Operation(summary = "生产原料使用记录列表")
    public ResultResponse ProduceMaterialList(@CurrentUser UserVo userVo, ProduceMaterialPageRequest request) {
        ResultResponse response = produceRecordService.produceMaterialList(userVo,request);
        return response;
    }

    @GetMapping("/productList")
    @Operation(summary = "产品列表(总览)")
    public ResultResponse ProductList(@CurrentUser UserVo userVo, ProductPageRequest request) {
        ResultResponse response = produceRecordService.totalList(userVo,request);
        return response;
    }
}