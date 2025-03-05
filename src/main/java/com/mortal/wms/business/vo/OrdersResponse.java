package com.mortal.wms.business.vo;

import com.mortal.wms.business.entity.Orders;
import com.mortal.wms.business.entity.OrderProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrdersResponse extends Orders {
    @Schema(description = "订单产品信息")
    private List<OrderProduct> orderProductList;
    @Schema(description = "客户公司")
    private String customerInfoName;
    @Schema(description = "订单总金额")
    private BigDecimal totalPrice;
}