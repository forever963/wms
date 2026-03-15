package com.mortal.wms.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "导出欠款对账单所需数据")
public class OweDataRequest {

    @Schema(description = "客户公司名")
    private String companyName;

    @Schema(description = "联系人名称")
    private String contactPerson;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "付款条件")
    private String paymentTerms;

    @Schema(description = "欠款订单信息列表")
    @Valid // 对列表中的每个 OweOrderProduct 对象进行校验
    private List<OweOrderProduct> oweOrderList;

    @Schema(description = "对当前月份欠款金额的小计")
    private BigDecimal currentTotalAmount;

    @Schema(description = "截止日期")
    private LocalDate cutoffDate;

    @Schema(description = "全部欠款金额")
    private BigDecimal totalOweAmount;

    @Schema(description = "备注1")
    private String remark1;

    @Schema(description = "备注2")
    private String remark2;

    @Schema(description = "对账单日期")
    private LocalDate statementDate;

    @Schema(description = "对月份欠款金额的小计")
    private Map<LocalDate, BigDecimal> monthlyBreakdown;
}