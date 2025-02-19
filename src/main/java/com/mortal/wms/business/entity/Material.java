package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "原料信息")
public class Material implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id; // 主键ID

    @Schema(description = "原料名", required = true, maxLength = 10)
    private String materialName;

    @Schema(description = "数量", required = true)
    private Integer quantity;

    @Schema(description = "单位", required = true, maxLength = 50, example = "吨")
    private String unit;

    @Schema(description = "税率")
    private Integer tax; // 税率

    @Schema(description = "含税单价", required = true)
    private BigDecimal unitPrice;

    @Schema(description = "含税总金额", required = true)
    private BigDecimal totalPrice;

    @Schema(description = "供货商ID", required = true)
    private Integer supplierId;

    @Schema(description = "订单发起时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderInitiatedTime;

    @Schema(description = "入库时间", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inboundTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单合同")
    private String orderContract;

    @Schema(description = "原料剩余", required = true)
    private Integer materialLeft;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime; // 创建时间

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime modifiedTime; // 修改时间

    @Schema(description = "删除时间")
    private LocalDateTime deletedTime; // 删除时间
}
