package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderPageRequest extends PageRequest {
    @Schema(description = "客户id", required = true)
    private String customerName; // 客户id
}
