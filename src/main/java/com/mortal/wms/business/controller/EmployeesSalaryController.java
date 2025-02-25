package com.mortal.wms.business.controller;

import com.mortal.wms.business.dto.EmployeesSalaryPageRequest;
import com.mortal.wms.business.entity.EmployeesSalary;
import com.mortal.wms.business.service.EmployeesSalaryService;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employeesSalary")
@Tag(name = "员工薪资")
public class EmployeesSalaryController {
    @Autowired
    private EmployeesSalaryService employeesSalaryService;

    @PostMapping
    @Operation(summary = "添加薪资记录")
    public ResultResponse addSalary(@RequestBody EmployeesSalary salary) {
        return employeesSalaryService.addSalary(salary);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除薪资记录")
    public ResultResponse deleteSalary(@PathVariable Integer id) {
        return employeesSalaryService.deleteSalary(id);
    }

    @PutMapping
    @Operation(summary = "更新薪资记录")
    public ResultResponse updateSalary(@RequestBody EmployeesSalary salary) {
        return employeesSalaryService.updateSalary(salary);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询薪资记录")
    public ResultResponse getSalaryById(@PathVariable Integer id) {
        return employeesSalaryService.getSalaryById(id);
    }

    @GetMapping
    @Operation(summary = "查询所有薪资记录")
    public ResultResponse listSalaries(EmployeesSalaryPageRequest request) {
        return employeesSalaryService.listSalaries(request);
    }
}