package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.EmployeesSalaryPageRequest;
import com.mortal.wms.business.entity.EmployeesSalary;
import com.mortal.wms.util.ResultResponse;

public interface EmployeesSalaryService extends IService<EmployeesSalary> {
    ResultResponse addSalary(EmployeesSalary salary);

    ResultResponse deleteSalary(Integer id);

    ResultResponse updateSalary(EmployeesSalary salary);

    ResultResponse getSalaryById(Integer id);

    ResultResponse listSalaries(EmployeesSalaryPageRequest request);
}
