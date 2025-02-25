package com.mortal.wms.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MaterialInboundRecordPageRequest extends PageRequest {
    @Schema(description = "原料名")
    private String materialName;
    @Schema(description = "供货商ID")
    private Integer supplierId;
    @Schema(description = "订单发起时间 开始")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String orderInitiatedTime;
    @Schema(description = "订单发起时间 结束")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String orderInitiatedTime1;
    @Schema(description = "入库时间 开始")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String inboundTime;
    @Schema(description = "入库时间 结束")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String inboundTime1;
    @Schema(description = "原料剩余")
    private Integer materialLeft;
}
