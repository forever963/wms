package com.mortal.wms.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
public class OweOrderRequest {
    //传递筛选信息  客户名 日期月月日开始-结束
    @Schema(description = "客户名")
    private String companyName;

    @Schema(description = "筛选开始时间")
    private LocalDate startDate;

    @Schema(description = "筛选结束时间")
    private LocalDate endDate;
}