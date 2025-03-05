package com.mortal.wms.business.dto;

import com.mortal.wms.business.entity.Orders;
import com.mortal.wms.business.entity.OrderProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
public class OrdersRequest extends Orders {
    @Schema(description = "订单-产品信息", required = true)
    List<OrderProduct> orderProductList;
}
