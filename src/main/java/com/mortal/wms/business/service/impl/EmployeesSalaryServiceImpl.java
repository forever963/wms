package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.EmployeesSalaryPageRequest;
import com.mortal.wms.business.entity.EmployeesSalary;
import com.mortal.wms.business.mapper.EmployeesSalaryMapper;
import com.mortal.wms.business.service.EmployeesSalaryService;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeesSalaryServiceImpl extends ServiceImpl<EmployeesSalaryMapper, EmployeesSalary> implements EmployeesSalaryService {
    @Autowired
    private EmployeesSalaryMapper employeesSalaryMapper;

    @Override
    @Transactional
    public ResultResponse addSalary(EmployeesSalary salary) {
        // 校验员工薪资记录是否已存在
        EmployeesSalary exist = employeesSalaryMapper.selectByEmployeeAndYearMonth(
                salary.getEmployeesId(), salary.getSalaryYear(), salary.getSalaryMouth());
        if (exist != null) {
            return ResultResponse.error("该员工本月的薪资记录已存在");
        }
        //验证薪资
        if(salary.getGrossSalary().subtract(salary.getAllowance())
                .subtract(salary.getPerformance())
                .subtract(salary.getOvertimePay())
                .subtract(salary.getSocialInsurance())
                .subtract(salary.getLoanDeduction())
                .subtract(salary.getTax())
                .compareTo(salary.getNetSalary())!=0){
            throw new BusinessException("实际到手工资计算有误");
        }
        int result = employeesSalaryMapper.insert(salary);
        return result > 0 ? ResultResponse.success() : ResultResponse.error();
    }

    @Override
    @Transactional
    public ResultResponse deleteSalary(Integer id) {
        EmployeesSalary salary = employeesSalaryMapper.selectById(id);
        if (salary == null) {
            return ResultResponse.error("薪资记录不存在");
        }
        salary.setDeletedTime(LocalDateTime.now());
        int result = employeesSalaryMapper.updateById(salary);
        return result > 0 ? ResultResponse.success() : ResultResponse.error();
    }

    @Override
    @Transactional
    public ResultResponse updateSalary(EmployeesSalary salary) {
        if (salary.getId() == null) {
            return ResultResponse.error("ID不能为空");
        }
        int result = employeesSalaryMapper.updateById(salary);
        return result > 0 ? ResultResponse.success() : ResultResponse.error();
    }

    @Override
    public ResultResponse getSalaryById(Integer id) {
        EmployeesSalary salary = employeesSalaryMapper.selectById(id);
        return salary != null ? ResultResponse.success(salary) : ResultResponse.error("薪资记录不存在");
    }

    @Override
    public ResultResponse listSalaries(EmployeesSalaryPageRequest request) {
        List<EmployeesSalary> salaries = employeesSalaryMapper.list(request);
        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), salaries);
        return ResultResponse.success(result);
    }
}
