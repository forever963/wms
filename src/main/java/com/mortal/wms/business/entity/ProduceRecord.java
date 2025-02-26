package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "生产记录")
@TableName("produce_record")
public class ProduceRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id; // 主键ID

    @Schema(description = "产品名")
    private String productName; // 产品名

    @Schema(description = "该次生产数量", required = true)
    private Integer produceQuantity;

    @Schema(description = "单位", required = true, maxLength = 50, example = "吨")
    private String unit;

    @Schema(description = "该次生产的产品总成本", required = true)
    private BigDecimal totalCost;

    @Schema(description = "剩余数量 为出库准备", defaultValue = "0")
    private Integer leftQuantity;

    @Schema(description = "备注")
    private String remark; // 备注

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime; // 创建时间

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime modifiedTime; // 修改时间

    @Schema(description = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedTime; // 删除时间
}
