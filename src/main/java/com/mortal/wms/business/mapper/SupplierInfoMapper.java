package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.SupplierInfoPageRequest;
import com.mortal.wms.business.entity.SupplierInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SupplierInfoMapper extends BaseMapper<SupplierInfo> {

    List<SupplierInfo> list(SupplierInfoPageRequest request);
}
