package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.SupplierInfoPageRequest;
import com.mortal.wms.business.entity.SupplierInfo;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;

public interface SupplierInfoService extends IService<SupplierInfo> {
    ResultResponse add(UserVo userVo, SupplierInfo supplierInfo);

    ResultResponse delete(UserVo userVo, Integer id);

    ResultResponse list(UserVo userVo, SupplierInfoPageRequest request);

    ResultResponse detail(UserVo userVo, Integer id);

    ResultResponse update(UserVo userVo, SupplierInfo supplierInfo);
}
