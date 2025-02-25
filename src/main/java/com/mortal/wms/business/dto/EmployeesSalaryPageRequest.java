package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmployeesSalaryPageRequest extends PageRequest {
    @Schema(description = "结算年份", required = true)
    private Integer salaryYear; // 结算年份

    @Schema(description = "结算月份", required = true)
    private Integer salaryMouth; // 结算月份
}
