package com.mortal.wms.business.controller;

import com.mortal.wms.business.entity.Employees;
import com.mortal.wms.business.service.EmployeesService;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@Tag(name = "员工")
public class EmployeesController {
    @Autowired
    private EmployeesService employeesService;
    

    @PostMapping
    @Operation(summary = "添加员工")
    public ResultResponse addEmployee(@RequestBody Employees employee) {
        return employeesService.addEmployee(employee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工")
    public ResultResponse deleteEmployee(@PathVariable Integer id) {
        return employeesService.deleteEmployee(id);
    }

    @PutMapping
    @Operation(summary = "更新员工信息")
    public ResultResponse updateEmployee(@RequestBody Employees employee) {
        return employeesService.updateEmployee(employee);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询员工")
    public ResultResponse getEmployeeById(@PathVariable Integer id) {
        return employeesService.getEmployeeById(id);
    }

    @GetMapping
    @Operation(summary = "查询所有员工")
    public ResultResponse listEmployees(PageRequest request) {
        return employeesService.listEmployees(request);
    }
}