package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("customer_info")
public class CustomerInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键", example = "1")
    private Integer id; // 主键

    @Schema(description = "客户公司名称", required = true, maxLength = 32)
    private String companyName; // 客户公司名称

    @Schema(description = "收货地址", required = true)
    private String shippingAddress; // 收货地址

    @Schema(description = "联系人姓名", required = true, maxLength = 32)
    private String contactPerson; // 联系人姓名

    @Schema(description = "联系电话", required = true, maxLength = 15)
    private String contactPhone; // 联系电话

    @Schema(description = "联系人电子邮箱地址", maxLength = 255)
    private String email; // 联系人电子邮箱地址

    @Schema(description = "传真号码", maxLength = 20)
    private String fax; // 传真号码

    @Schema(description = "办公电话号码", maxLength = 15)
    private String officePhone; // 办公电话号码

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