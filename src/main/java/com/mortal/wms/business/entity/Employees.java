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
@Schema(description = "员工表")
public class Employees implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id", example = "1")
    private Integer id; // 主键id

    @Schema(description = "身份证", required = true, maxLength = 18)
    private String idCard; // 身份证

    @Schema(description = "姓名", required = true, maxLength = 10)
    private String name; // 姓名

    @Schema(description = "性别", required = true)
    private String gender; // 性别

    @Schema(description = "民族", maxLength = 10)
    private String nation; // 民族

    @Schema(description = "职位", maxLength = 10)
    private String position; // 职位

    @Schema(description = "籍贯", maxLength = 30)
    private String nativePlace; // 籍贯

    @Schema(description = "手机号", maxLength = 11)
    private String phone; // 手机号

    @Schema(description = "员工底薪")
    private BigDecimal baseSalary; // 员工底薪

    @Schema(description = "合同起始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractStartDate; // 合同起始日期

    @Schema(description = "合同结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime contractEndDate; // 合同结束日期

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
