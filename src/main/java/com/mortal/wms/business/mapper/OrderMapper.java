package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.OrderPageRequest;
import com.mortal.wms.business.entity.Orders;
import com.mortal.wms.business.vo.OrdersResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

    List<OrdersResponse> list(OrderPageRequest request);
}
