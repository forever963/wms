package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.OrderOutBoundPageRequest;
import com.mortal.wms.business.entity.ProductOutboundRecord;
import com.mortal.wms.business.vo.ProductOutboundRecordResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductOutboundRecordMapper extends BaseMapper<ProductOutboundRecord> {
    List<ProductOutboundRecordResponse> list(OrderOutBoundPageRequest request);
}
