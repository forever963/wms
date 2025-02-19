package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.dto.MaterialPageRequest;
import com.mortal.wms.business.entity.Material;
import com.mortal.wms.business.service.MaterialService;
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
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    @Operation(summary = "新增")
    @PostMapping("/add")
    public ResultResponse addMaterial(@CurrentUser UserVo userVo, @RequestBody Material material) {
        ResultResponse resultResponse = materialService.add(userVo,material);
        return resultResponse;
    }

    @Operation(summary = "详情")
    @GetMapping("/detail/{id}")
    public ResultResponse detail(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse resultResponse = materialService.detail(userVo,id);
        return resultResponse;
    }

    @Operation(summary = "列表")
    @GetMapping("list")
    public ResultResponse list(@CurrentUser UserVo userVo,MaterialPageRequest request) {
        ResultResponse resultResponse = materialService.list(userVo,request);
        return resultResponse;
    }

    @Operation(summary = "原料入库")
    @GetMapping("/inbound/{id}")
    public ResultResponse inbound(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse resultResponse = materialService.inbound(userVo,id);
        return resultResponse;
    }

    @Operation(summary = "删除")
    @GetMapping("/delete/{id}")
    private ResultResponse deleteMaterial(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse resultResponse = materialService.delete(userVo,id);
        return resultResponse;
    }

    @Operation(summary = "原料总览")
    @GetMapping("/total")
    private ResultResponse total(@CurrentUser UserVo userVo,MaterialPageRequest request) {
        ResultResponse resultResponse = materialService.total(userVo,request);
        return resultResponse;
    }
}