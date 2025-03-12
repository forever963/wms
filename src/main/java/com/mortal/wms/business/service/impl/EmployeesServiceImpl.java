package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.entity.Employees;
import com.mortal.wms.business.mapper.EmployeesMapper;
import com.mortal.wms.business.service.EmployeesService;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeesServiceImpl extends ServiceImpl<EmployeesMapper,Employees> implements EmployeesService {
    @Autowired
    private EmployeesMapper employeesMapper;

    @Override
    @Transactional
    public ResultResponse addEmployee(Employees employee) {
        // 校验身份证和手机号是否重复
        Employees existByIdCard = employeesMapper.selectByIdCard(employee.getIdCard());
        if (existByIdCard != null) {
            return ResultResponse.error("身份证号已存在");
        }

        Employees existByPhone = employeesMapper.selectByPhone(employee.getPhone());
        if (existByPhone != null) {
            return ResultResponse.error("手机号已存在");
        }
        employee.setCreatedTime(LocalDateTime.now());
        int result = employeesMapper.insert(employee);
        return result > 0 ? ResultResponse.success() : ResultResponse.error("添加失败");
    }

    @Override
    @Transactional
    public ResultResponse deleteEmployee(Integer id) {
        Employees employee = employeesMapper.selectById(id);
        if (employee == null) {
            return ResultResponse.error("员工不存在");
        }

        // 逻辑删除：设置删除时间
        employee.setDeletedTime(LocalDateTime.now());
        int result = employeesMapper.updateById(employee);
        return result > 0 ? ResultResponse.success("删除成功") : ResultResponse.error("删除失败");
    }

    @Override
    @Transactional
    public ResultResponse updateEmployee(Employees employee) {
        if (employee.getId() == null) {
            return ResultResponse.error("ID不能为空");
        }

        // 校验手机号是否与其他员工重复
        Employees existByPhone = employeesMapper.selectByPhone(employee.getPhone());
        if (existByPhone != null && !existByPhone.getId().equals(employee.getId())) {
            return ResultResponse.error("手机号已存在");
        }

        int result = employeesMapper.updateById(employee);
        return result > 0 ? ResultResponse.success("更新成功", employee) : ResultResponse.error("更新失败");
    }

    @Override
    public ResultResponse getEmployeeById(Integer id) {
        Employees employee = employeesMapper.selectById(id);
        return employee != null ? ResultResponse.success(employee) : ResultResponse.error("员工不存在");
    }

    @Override
    public ResultResponse listEmployees(PageRequest request) {
        List<Employees> employees = employeesMapper.selectList(new LambdaQueryWrapper<Employees>()
                .isNull(Employees::getDeletedTime)
                .orderByDesc(Employees::getCreatedTime)
        );
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(),request.getPageSize(),employees);
        return ResultResponse.success(pageResult);
    }
}