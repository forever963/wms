package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.entity.CustomerInfo;
import com.mortal.wms.business.mapper.CustomerInfoMapper;
import com.mortal.wms.business.service.CustomerInfoService;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {
    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Override
    @Transactional
    public ResultResponse addCustomer(CustomerInfo customer) {
        customer.setCreatedTime(LocalDateTime.now());
        // 校验公司名称是否重复
        CustomerInfo existCompany = customerInfoMapper.selectByCompanyName(customer.getCompanyName());
        if (existCompany != null) {
            return ResultResponse.error("公司名称已存在");
        }

        // 校验联系电话是否重复
        CustomerInfo existPhone = customerInfoMapper.selectByContactPhone(customer.getContactPhone());
        if (existPhone != null) {
            return ResultResponse.error("联系电话已存在");
        }

        int result = customerInfoMapper.insert(customer);
        return result > 0 ? ResultResponse.success() : ResultResponse.error();
    }

    @Override
    @Transactional
    public ResultResponse deleteCustomer(Integer id) {
        CustomerInfo customer = customerInfoMapper.selectById(id);
        if (customer == null) {
            return ResultResponse.error("客户不存在");
        }

        // 逻辑删除：设置删除时间
        customer.setDeletedTime(LocalDateTime.now());
        int result = customerInfoMapper.updateById(customer);
        return result > 0 ? ResultResponse.success() : ResultResponse.error();
    }

    @Override
    @Transactional
    public ResultResponse updateCustomer(CustomerInfo customer) {
        if (customer.getId() == null) {
            return ResultResponse.error("ID不能为空");
        }

        // 校验联系电话是否与其他客户重复
        CustomerInfo existPhone = customerInfoMapper.selectByContactPhone(customer.getContactPhone());
        if (existPhone != null && !existPhone.getId().equals(customer.getId())) {
            return ResultResponse.error("联系电话已存在");
        }
        customer.setModifiedTime(LocalDateTime.now());
        int result = customerInfoMapper.updateById(customer);
        return result > 0 ? ResultResponse.success() : ResultResponse.error();
    }

    @Override
    public ResultResponse getCustomerById(Integer id) {
        CustomerInfo customer = customerInfoMapper.selectById(id);
        if (customer != null || customer.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        return ResultResponse.success(customer);
    }

    @Override
    public ResultResponse listCustomers(PageRequest request) {
        List<CustomerInfo> customers = customerInfoMapper.selectList(new LambdaQueryWrapper<CustomerInfo>()
                .isNull(CustomerInfo::getDeletedTime)
        );
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), customers);
        return ResultResponse.success(pageResult);
    }
}
