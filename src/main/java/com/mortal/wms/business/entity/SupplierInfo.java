package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "供货商")
@Data
public class SupplierInfo implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id;

    @Schema(description = "供货商名称", required = true, maxLength = 32, example = "ABC公司")
    private String supplierName;

    @Schema(description = "供货商联系人姓名", maxLength = 255)
    private String supplierContactPerson;

    @Schema(description = "供货商联系方式", required = true, maxLength = 11, example = "12345678998")
    private String supplierContact;

    @Schema(description = "供货商地址", maxLength = 32)
    private String supplierAddress;

    @Schema(description = "开户银行", maxLength = 32)
    private String bankName;

    @Schema(description = "银行账号", maxLength = 32)
    private String bankAccount;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime modifiedTime;

    @Schema(description = "删除时间")
    private LocalDateTime deletedTime;


}
