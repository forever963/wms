package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.entity.ProduceRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProduceRecordMapper extends BaseMapper<ProduceRecord> {
    List<ProduceRecord> list();

    List<ProduceRecord> total();
}
