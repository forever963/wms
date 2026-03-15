package com.mortal.wms.business.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Data
public class OweOrderResponse {
    List<OrdersResponse> currentPeriodOrders;
    List<OrdersResponse> historyOrders;
    Map<YearMonth, BigDecimal> monthlyBreakdown;
    BigDecimal currentTotalAmount;
}
