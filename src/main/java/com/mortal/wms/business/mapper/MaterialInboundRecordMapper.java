package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.MaterialInboundRecordPageRequest;
import com.mortal.wms.business.entity.MaterialInboundRecord;
import com.mortal.wms.business.vo.MaterialInboundRecordResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MaterialInboundRecordMapper extends BaseMapper<MaterialInboundRecord> {

    List<MaterialInboundRecordResponse> list(MaterialInboundRecordPageRequest request);

    List<MaterialInboundRecordResponse> totalList(MaterialInboundRecordPageRequest request);

    @Select("select sum(material_left) from material_inbound_record " +
            "where material_name = #{materialName} and supplier_id = #{supplierId} and deleted_time is null and material_left >0")
    Integer getTotalQuantity(String materialName, Integer supplierId);
}
