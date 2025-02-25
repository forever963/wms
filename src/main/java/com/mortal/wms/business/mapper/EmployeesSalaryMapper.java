package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.dto.EmployeesSalaryPageRequest;
import com.mortal.wms.business.entity.EmployeesSalary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeesSalaryMapper extends BaseMapper<EmployeesSalary> {
    List<EmployeesSalary> list(EmployeesSalaryPageRequest request);

    @Select("SELECT * FROM employees_salary WHERE employees_id = #{employeesId} AND salary_year = #{year} AND salary_mouth = #{month}")
    EmployeesSalary selectByEmployeeAndYearMonth(@Param("employeesId") Integer employeesId, @Param("year") Integer year, @Param("month") Integer month);
}
