package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.MaterialInboundRecordPageRequest;
import com.mortal.wms.business.entity.MaterialInboundRecord;
import com.mortal.wms.business.vo.MaterialInboundRecordResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialInboundRecordMapper extends BaseMapper<MaterialInboundRecord> {

    List<MaterialInboundRecordResponse> list(MaterialInboundRecordPageRequest request);

    List<MaterialInboundRecordResponse> totalList(MaterialInboundRecordPageRequest request);
}
