package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerPageRequest extends PageRequest {
    @Schema(description = "客户公司名称", required = true)
    private String companyName; // 客户公司名称
}
