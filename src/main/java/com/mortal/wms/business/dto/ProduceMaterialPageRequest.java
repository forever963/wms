package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProduceMaterialPageRequest extends PageRequest {
    @Schema(description = "原料名")
    private String materialName;
    @Schema(description = "创建时间 开始")
    private LocalDateTime createdTime;
    @Schema(description = "创建时间 结束")
    private LocalDateTime createdTime1;
}
