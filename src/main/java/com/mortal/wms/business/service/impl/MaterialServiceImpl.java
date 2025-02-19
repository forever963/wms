package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.MaterialPageRequest;
import com.mortal.wms.business.entity.Material;
import com.mortal.wms.business.entity.SupplierInfo;
import com.mortal.wms.business.mapper.MaterialMapper;
import com.mortal.wms.business.mapper.SupplierInfoMapper;
import com.mortal.wms.business.service.MaterialService;
import com.mortal.wms.business.vo.MaterialResponse;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {
    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private SupplierInfoMapper supplierInfoMapper;

    @Override
    @Transactional
    public ResultResponse add(UserVo userVo, Material material) {
        //验证供货商是否存在
        SupplierInfo supplierInfo = supplierInfoMapper.selectById(material.getSupplierId());
        if (supplierInfo == null || supplierInfo.getDeletedTime() != null) {
            throw new BusinessException("该供货商不存在,请检查并重新提交");
        }
        //验证单位 入库单位必须是kg
        if (material.getUnit().equals("吨")) {
            material.setQuantity(material.getQuantity() * 1000);
            material.setUnitPrice(material.getUnitPrice().divide(new BigDecimal(1000)));
            material.setUnit("kg");
        }
        //验证税率 税费
        if(material.getUnitPrice().multiply(new BigDecimal(material.getQuantity())).compareTo(material.getTotalPrice()) != 0){
            throw new BusinessException("含税总金额计算错误 请检查并重新提交");
        }
        material.setCreatedTime(LocalDateTime.now());
        materialMapper.insert(material);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse detail(UserVo userVo, Integer id) {
        Material old = materialMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        MaterialResponse response = new MaterialResponse();
        response.setSupplierName(supplierInfoMapper.selectById(old.getSupplierId()).getSupplierName());
        BeanUtils.copyProperties(old, response);
        return ResultResponse.success(response);
    }

    @Override
    public ResultResponse list(UserVo userVo, MaterialPageRequest request) {
        List<MaterialResponse> responseList = materialMapper.list(request);
        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), responseList);
        return ResultResponse.success(result);
    }

    @Override
    public ResultResponse inbound(UserVo userVo, Integer id) {
        Material old = materialMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        old.setInboundTime(LocalDateTime.now());
        old.setMaterialLeft(old.getQuantity());
        old.setStatus("已入库");
        materialMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse delete(UserVo userVo, Integer id) {
        Material old = materialMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        if (old.getStatus().equals("用尽")) {
            throw new BusinessException("该记录的原料未使用完,不可删除");
        }
        old.setDeletedTime(LocalDateTime.now());
        materialMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse total(UserVo userVo, MaterialPageRequest request) {
        //获取同一原料名 通同一供货商的记录
        List<MaterialResponse> list = materialMapper.totalList(request);

        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }
}
