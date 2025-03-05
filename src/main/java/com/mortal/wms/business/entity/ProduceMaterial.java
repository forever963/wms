package com.mortal.wms.business.entity;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "生产原料使用记录")
@TableName("produce_material")
public class ProduceMaterial implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id;

    @Schema(description = "原料入库记录id", required = false)
    private Integer materialInboundRecordId;

    @Schema(description = "原料名", required = true)
    private String materialName;

    @Schema(description = "供货商id", required = true)
    private Integer supplierId;

    @Schema(description = "生产记录ID 不填")
    private Integer produceRecordId;

    @Schema(description = "使用数量", required = true)
    private Integer quantityUsed;

    @Schema(description = "单位", required = true, maxLength = 50, example = "T/KG")
    private String unit;

    @Schema(description = "单位成本", required = true)
    private BigDecimal costPerUnit;

    @Schema(description = "该项成本总计")
    private BigDecimal totalCost;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime; // 创建时间

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime modifiedTime; // 修改时间

    @Schema(description = "删除时间")
    private LocalDateTime deletedTime; // 删除时间
}
