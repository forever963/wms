package com.mortal.wms.business.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mortal.wms.business.entity.ProductOutboundRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductOutboundRecordResponse extends ProductOutboundRecord {
    @Schema(description = "产品名", required = true, maxLength = 32)
    private String productName;
    @Schema(description = "生产时间", required = true, maxLength = 32)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime produceTime;
    @Schema(description = "订单编号")
    private String orderNum;
    @Schema(description = "客户公司名称", required = true, maxLength = 32)
    private String companyName;
}