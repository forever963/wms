package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "产品出库记录表")
public class ProductOutboundRecord implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id;

    @Schema(description = "入库记录id", required = true)
    private Integer produceRecordId;

    @Schema(description = "订单产品id", required = true)
    private Integer orderProductId;

    @Schema(description = "数量", required = true)
    private Integer quantity;

    @Schema(description = "单位", defaultValue = "KG")
    private String unit;

    @Schema(description = "出库时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outboundTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime modifiedTime;

    @Schema(description = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedTime;
}