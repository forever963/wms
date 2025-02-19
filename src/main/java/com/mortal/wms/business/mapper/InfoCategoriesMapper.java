package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.entity.InfoCategories;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InfoCategoriesMapper extends BaseMapper<InfoCategories> {
    @Select("select * from info_categories where type = #{type} order by display_order")
    List<InfoCategories> list(String type);
}