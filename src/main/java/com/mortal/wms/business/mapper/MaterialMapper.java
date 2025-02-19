package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.MaterialPageRequest;
import com.mortal.wms.business.entity.Material;
import com.mortal.wms.business.vo.MaterialResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    List<MaterialResponse> list(MaterialPageRequest request);

    List<MaterialResponse> totalList(MaterialPageRequest request);
}
