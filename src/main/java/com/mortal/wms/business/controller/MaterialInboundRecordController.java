package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.dto.MaterialInboundRecordPageRequest;
import com.mortal.wms.business.entity.MaterialInboundRecord;
import com.mortal.wms.business.service.MaterialInboundRecordService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/material")
@Slf4j
@Tag(name = "原料",description = "原料接口")
public class MaterialInboundRecordController {
    @Autowired
    private MaterialInboundRecordService materialInboundRecordService;

    @Operation(summary = "新增")
    @PostMapping("/add")
    public ResultResponse addMaterial(@CurrentUser UserVo userVo, @RequestBody MaterialInboundRecord materialInboundRecord) {
        ResultResponse resultResponse = materialInboundRecordService.add(userVo,materialInboundRecord);
        return resultResponse;
    }

    @Operation(summary = "详情")
    @GetMapping("/detail/{id}")
    public ResultResponse detail(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse resultResponse = materialInboundRecordService.detail(userVo,id);
        return resultResponse;
    }

    @Operation(summary = "列表")
    @GetMapping("list")
    public ResultResponse list(@CurrentUser UserVo userVo, MaterialInboundRecordPageRequest request) {
        ResultResponse resultResponse = materialInboundRecordService.list(userVo,request);
        return resultResponse;
    }

    @Operation(summary = "原料入库")
    @GetMapping("/inbound/{id}")
    public ResultResponse inbound(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse resultResponse = materialInboundRecordService.inbound(userVo,id);
        return resultResponse;
    }

    @Operation(summary = "删除")
    @GetMapping("/delete/{id}")
    private ResultResponse deleteMaterial(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse resultResponse = materialInboundRecordService.delete(userVo,id);
        return resultResponse;
    }

    @Operation(summary = "原料总览")
    @GetMapping("/total")
    private ResultResponse total(@CurrentUser UserVo userVo, MaterialInboundRecordPageRequest request) {
        ResultResponse resultResponse = materialInboundRecordService.total(userVo,request);
        return resultResponse;
    }
}