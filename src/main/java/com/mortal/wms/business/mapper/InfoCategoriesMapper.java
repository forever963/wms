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

    @Select("select count(*) from info_categories where name = #{name}")
    Integer get(String name);

    @Select("select name from info_categories where type = #{type}")
    List<String> getNamesByType(Integer type);
}