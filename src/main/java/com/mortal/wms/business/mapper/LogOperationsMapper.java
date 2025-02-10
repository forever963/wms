package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mortal.wms.business.dto.LogOperationsRequest;
import com.mortal.wms.business.entity.LogOperations;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogOperationsMapper extends BaseMapper<LogOperations> {

    IPage<LogOperations> select(IPage<LogOperations> iPage, LogOperationsRequest request);
}
