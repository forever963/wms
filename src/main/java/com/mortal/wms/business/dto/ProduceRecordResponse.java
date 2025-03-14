package com.mortal.wms.business.dto;

import com.mortal.wms.business.entity.ProduceRecord;
import com.mortal.wms.business.vo.ProductMaterialResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ProduceRecordResponse extends ProduceRecord {
    @Schema(description = "消耗的原料")
    List<ProductMaterialResponse> productMaterialResponseList;
}
