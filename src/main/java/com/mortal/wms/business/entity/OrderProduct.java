package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单-产品")
@TableName("order_product")
public class OrderProduct implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键", example = "1")
    private Integer id; // 主键

    @Schema(description = "订单id", required = true)
    private Integer orderId; // 订单id

    @Schema(description = "产品名", required = true, maxLength = 32)
    private String productName; // 产品名

    @Schema(description = "数量", required = true)
    private Integer quantity; // 数量

    @Schema(description = "单位", defaultValue = "KG")
    private String unit; // 单位

    @Schema(description = "单价", required = true)
    private BigDecimal unitPrice; // 单价

    @Schema(description = "已出库数量", defaultValue = "0")
    private Integer outboundQuantity; // 已出库数量

    @Schema(description = "创建时间", required = true)
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