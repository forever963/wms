package com.mortal.wms.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
public class OutBoundProduct implements Serializable {
    @Schema(description = "生产记录id")
    private Integer ProduceRecordId;
    @Schema(description = "数量")
    private Integer quantity;
    @Schema(description = "单位", defaultValue = "KG")
    private String unit;
}