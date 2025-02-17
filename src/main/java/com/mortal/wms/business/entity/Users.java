package com.mortal.wms.business.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.xiaoymin.knife4j.annotations.Ignore;
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

    @Schema(description = "姓名", maxLength = 64, required = true)
    private String name; // 姓名

    @Schema(description = "密码")
    @TableField(updateStrategy = FieldStrategy.IGNORED) //不被包含在更新语句中
    private String password; // 密码

    @Schema(description = "职位", maxLength = 10)
    private String position; // 职位

    @Schema(description = "手机", maxLength = 11)
    @TableField(updateStrategy = FieldStrategy.IGNORED) //不被包含在更新语句中
    private String phone; // 手机

    @Schema(description = "单点登录token")
    private String ssoToken;

    @Schema(description = "token过期时间")
    private LocalDateTime ssoExpireTime; // token过期时间

    @Schema(description = "是否超管")
    @TableField(updateStrategy = FieldStrategy.IGNORED) //不被包含在更新语句中
    private Boolean administrator;

    @Schema(description = "创建时间")
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT,updateStrategy = FieldStrategy.IGNORED)//自动插入创建时间  更新忽略
    private LocalDateTime createdTime; // 创建时间

    @Schema(description = "修改时间")
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifiedTime; // 修改时间

    @Schema(description = "删除时间")
    @JsonIgnore
    private LocalDateTime deletedTime; // 删除时间
}
