package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.entity.OrderProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface OrderProductMapper extends BaseMapper<OrderProduct> {

    @Select("select sum(op.quantity * op.unit_price) from order_product op where order_id = #{orderId} and deleted_time is null")
    BigDecimal getTotalByOrderId(Integer orderId);
}
