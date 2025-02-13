package com.mortal.wms.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mortal.wms.business.dto.LogOperationsRequest;
import com.mortal.wms.business.entity.LogOperations;
import com.mortal.wms.business.service.LogOperationsService;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "操作日志")
public class LogOperationsController {
    @Autowired
    private LogOperationsService logOperationsService;

    @Operation(summary = "详情")
    @GetMapping(value = "logOperations")
    public ResultResponse<IPage<LogOperations>> select(LogOperationsRequest request){
        return logOperationsService.select(request);
    }
}