package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.entity.Employees;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;

public interface EmployeesService extends IService<Employees> {
    ResultResponse addEmployee(Employees employee);

    ResultResponse deleteEmployee(Integer id);

    ResultResponse getEmployeeById(Integer id);

    ResultResponse updateEmployee(Employees employee);

    ResultResponse listEmployees(PageRequest request);
}
