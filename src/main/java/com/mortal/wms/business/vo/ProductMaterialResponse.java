package com.mortal.wms.business.vo;

import com.mortal.wms.business.entity.ProduceMaterial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ProductMaterialResponse extends ProduceMaterial {
    @Schema(description = "原料名")
    private String materialName;
    @Schema(description = "供货商名")
    private String supplierName;
}