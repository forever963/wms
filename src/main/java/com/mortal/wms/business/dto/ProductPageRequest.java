package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductPageRequest extends PageRequest {
    @Schema(description = "产品名")
    private String productName;
}
