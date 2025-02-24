package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.entity.Employees;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeesMapper extends BaseMapper<Employees> {
    @Select("SELECT * FROM employees WHERE id_card = #{idCard}")
    Employees selectByIdCard(@Param("idCard") String idCard);

    @Select("SELECT * FROM employees WHERE phone = #{phone}")
    Employees selectByPhone(@Param("phone") String phone);
}
