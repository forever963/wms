package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LogOperationsRequest extends PageRequest implements Serializable {
    @Schema(name = "用户id")
    private Long userId;
    @Schema(name = "内容")
    private String content;
    @Schema(name = "创建时间start")
    private LocalDateTime createdTimeStart;
    @Schema(name = "创建时间end")
    private LocalDateTime createdTimeEnd;
}
