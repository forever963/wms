package com.mortal.wms.business.dto;

import com.mortal.wms.business.entity.ProduceMaterial;
import com.mortal.wms.business.entity.ProduceRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProduceRecordRequest extends ProduceRecord implements Serializable {
    @Schema(description = "原料使用列表",required = true)
    private List<ProduceMaterial> produceMaterialList;
}
