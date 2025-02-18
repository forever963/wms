package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.SupplierInfoPageRequest;
import com.mortal.wms.business.entity.SupplierInfo;
import com.mortal.wms.business.mapper.SupplierInfoMapper;
import com.mortal.wms.business.service.SupplierInfoService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class SupplierInfoServiceImpl extends ServiceImpl<SupplierInfoMapper, SupplierInfo> implements SupplierInfoService {
    @Autowired
    private SupplierInfoMapper supplierInfoMapper;

    @Override
    public ResultResponse add(UserVo userVo, SupplierInfo supplierInfo) {
        supplierInfo.setCreatedTime(LocalDateTime.now());
        supplierInfoMapper.insert(supplierInfo);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse delete(UserVo userVo, Integer id) {
        SupplierInfo old = supplierInfoMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        old.setDeletedTime(LocalDateTime.now());
        supplierInfoMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse list(UserVo userVo, SupplierInfoPageRequest request) {
        List<SupplierInfo> list = supplierInfoMapper.list(request);

        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(),request.getPageSize(),list);
        return ResultResponse.success(pageResult);
    }

    @Override
    public ResultResponse detail(UserVo userVo, Integer id) {
        SupplierInfo old = supplierInfoMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("该记录不存在");
        }
        return ResultResponse.success(old);
    }

    @Override
    public ResultResponse update(UserVo userVo, SupplierInfo supplierInfo) {
        SupplierInfo old = supplierInfoMapper.selectById(supplierInfo.getId());
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        old.setModifiedTime(LocalDateTime.now());
        BeanUtils.copyProperties(supplierInfo, old);
        supplierInfoMapper.updateById(old);
        return ResultResponse.success();
    }
}
