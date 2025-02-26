package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.ProduceMaterialPageRequest;
import com.mortal.wms.business.entity.ProduceMaterial;
import com.mortal.wms.business.vo.ProductMaterialResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProduceMaterialMapper extends BaseMapper<ProduceMaterial> {
    List<ProductMaterialResponse> list(ProduceMaterialPageRequest request);
}
