package com.mortal.wms.business.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "用户表")
public class Users implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id")
    private Long id; // 主键id

    @Schema(description = "身份证", maxLength = 18, required = true)
    @TableField(value = "id_card", fill = FieldFill.INSERT)
    private String idCard; // 身份证

    @Schema(description = "姓名", maxLength = 64, required = true)
    private String name; // 姓名

    @Schema(description = "性别", allowableValues = {"男", "女"}, required = true)
    private String gender; // 性别

    @Schema(description = "民族", maxLength = 10)
    private String nation; // 民族

    @Schema(description = "密码")
    @JSONField(serialize=false)
    private String password; // 密码

    @Schema(description = "职位", maxLength = 10)
    private String position; // 职位

    @Schema(description = "籍贯", maxLength = 30)
    private String nativePlace; // 籍贯

    @Schema(description = "手机", maxLength = 11)
    private String phone; // 手机

    @Schema(description = "单点登录token")
    private String ssoToken;

    @Schema(description = "token过期时间")
    private LocalDateTime ssoExpireTime; // token过期时间

    @Schema(description = "合同起始日期")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime contractStartDate; // 合同起始日期

    @Schema(description = "合同结束日期")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime contractEndDate; // 合同结束日期

    @Schema(description = "是否超管")
    private Boolean administrator;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime; // 创建时间

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedTime; // 修改时间

    @Schema(description = "删除时间")
    private LocalDateTime deletedTime; // 删除时间
}
