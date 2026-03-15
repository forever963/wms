package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderPageRequest extends PageRequest {
    @Schema(description = "客户id", required = true)
    private String companyName; // 客户id

    @Schema(description = "筛选开始时间")
    private LocalDate startDate;

    @Schema(description = "筛选结束时间")
    private LocalDate endDate;

    @Schema(description = "是否欠款")
    private Boolean isOwe;
}
