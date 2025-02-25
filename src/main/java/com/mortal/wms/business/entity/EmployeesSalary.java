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
@Schema(description = "员工薪资结算表")
public class EmployeesSalary implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键id", example = "1")
    private Integer id; // 主键id

    @Schema(description = "员工id", required = true)
    private Integer employeesId; // 员工id

    @Schema(description = "结算年份", required = true)
    private Integer salaryYear; // 结算年份

    @Schema(description = "结算月份", required = true)
    private Integer salaryMouth; // 结算月份

    @Schema(description = "补贴（餐补、交通补等）", defaultValue = "0")
    private BigDecimal allowance; // 补贴

    @Schema(description = "业绩提成", defaultValue = "0")
    private BigDecimal performance; // 业绩提成

    @Schema(description = "奖金", defaultValue = "0")
    private BigDecimal overtimePay; // 奖金

    @Schema(description = "缺勤", defaultValue = "0")
    private BigDecimal absence; // 缺勤

    @Schema(description = "应发工资", defaultValue = "0")
    private BigDecimal grossSalary; // 应发工资

    @Schema(description = "社保扣除", defaultValue = "0")
    private BigDecimal socialInsurance; // 社保扣除

    @Schema(description = "借扣款", defaultValue = "0")
    private BigDecimal loanDeduction; // 借扣款

    @Schema(description = "个税扣除", defaultValue = "0")
    private BigDecimal tax; // 个税扣除

    @Schema(description = "实际到手薪资（计算字段）", required = true)
    private BigDecimal netSalary; // 实际到手薪资

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