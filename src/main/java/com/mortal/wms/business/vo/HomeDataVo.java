package com.mortal.wms.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class HomeDataVo implements Serializable {
    @Schema(description = "年度订单总金额")
    BigDecimal annualOrderTotal;
    @Schema(description = "年度采购总金额")
    BigDecimal restockAnnualTotal;
    @Schema(description = "客户数量")
    Integer customerNumber;
    @Schema(description = "供货商数量")
    Integer supplierNumber;
    @Schema(description = "月度订单总额")
    Map<Integer,BigDecimal> monthlyOrderTotal;
    @Schema(description = "年度总收款")
    BigDecimal incomeTotal;
    @Schema(description = "年度总支出")
    BigDecimal expenseTotal;
    @Schema(description = "收款")
    Map<Integer,BigDecimal> income;
    @Schema(description = "支出")
    Map<Integer,BigDecimal> expense;
}
