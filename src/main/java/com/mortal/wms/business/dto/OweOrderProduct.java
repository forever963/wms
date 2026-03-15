package com.mortal.wms.business.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mortal.wms.business.entity.OrderProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OweOrderProduct extends OrderProduct {
    @Schema(description = "送货单号")
    private String deliveryNoteNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "订单时间")
    private LocalDate date;
    @Schema(description = "送货单号（全）")
    private String orderNum;
}