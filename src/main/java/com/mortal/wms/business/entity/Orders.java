package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.swing.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订单管理")
@TableName("orders")
public class Orders implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键", example = "1")
    private Integer id; // 主键

    @Schema(description = "订单编号")
    private String orderNum;

    @Schema(description = "客户id", required = true)
    private Integer customerInfoId;

    @Schema(description = "是否含税")
    private String tax; // 是否含税

    @Schema(description = "付款方式")
    private String paymentMethod;

    @Schema(description = "已付", defaultValue = "0")
    private BigDecimal paidAmount;

    @Schema(description = "订单创建时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderCreationTime; // 订单创建时间

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