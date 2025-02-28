package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.entity.CustomerInfo;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.ResultResponse;

public interface CustomerInfoService extends IService<CustomerInfo> {
    ResultResponse addCustomer(CustomerInfo customer);

    ResultResponse deleteCustomer(Integer id);

    ResultResponse updateCustomer(CustomerInfo customer);

    ResultResponse getCustomerById(Integer id);

    ResultResponse listCustomers(PageRequest request);
}