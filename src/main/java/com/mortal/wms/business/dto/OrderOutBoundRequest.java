package com.mortal.wms.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderOutBoundRequest implements Serializable {
    @Schema(description = "订单产品id", required = true)
    private Integer orderProductId;
    @Schema(description = "出库产品")
    List<OutBoundProduct> outBoundProductList;
    @Schema(description = "出库时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outboundTime;
}