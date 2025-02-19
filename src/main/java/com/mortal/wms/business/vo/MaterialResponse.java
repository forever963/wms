package com.mortal.wms.business.vo;

import com.mortal.wms.business.entity.Material;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class MaterialResponse extends Material implements Serializable {
    @Schema(description = "供货商名称", required = true, maxLength = 32, example = "ABC公司")
    private String supplierName;
}
