package com.mortal.wms.business.vo;

import com.mortal.wms.business.entity.MaterialInboundRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class MaterialInboundRecordResponse extends MaterialInboundRecord implements Serializable {
    @Schema(description = "供货商名称", required = true, maxLength = 32, example = "ABC公司")
    private String supplierName;
}
