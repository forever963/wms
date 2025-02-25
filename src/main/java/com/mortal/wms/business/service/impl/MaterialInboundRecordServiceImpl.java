package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.MaterialInboundRecordPageRequest;
import com.mortal.wms.business.entity.InfoCategories;
import com.mortal.wms.business.entity.MaterialInboundRecord;
import com.mortal.wms.business.entity.SupplierInfo;
import com.mortal.wms.business.mapper.InfoCategoriesMapper;
import com.mortal.wms.business.mapper.MaterialInboundRecordMapper;
import com.mortal.wms.business.mapper.SupplierInfoMapper;
import com.mortal.wms.business.service.MaterialInboundRecordService;
import com.mortal.wms.business.vo.MaterialInboundRecordResponse;
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
import java.util.List;

@Service
@Slf4j
public class MaterialInboundRecordServiceImpl extends ServiceImpl<MaterialInboundRecordMapper, MaterialInboundRecord> implements MaterialInboundRecordService {
    @Autowired
    private MaterialInboundRecordMapper materialInboundRecordMapper;
    @Autowired
    private SupplierInfoMapper supplierInfoMapper;
    @Autowired
    private InfoCategoriesMapper infoCategoriesMapper;

    @Override
    @Transactional
    public ResultResponse add(UserVo userVo, MaterialInboundRecord materialInboundRecord) {
        //验证供货商是否存在
        SupplierInfo supplierInfo = supplierInfoMapper.selectById(materialInboundRecord.getSupplierId());
        if (supplierInfo == null || supplierInfo.getDeletedTime() != null) {
            throw new BusinessException("该供货商不存在,请检查并重新提交");
        }
        if (infoCategoriesMapper.get(materialInboundRecord.getMaterialName()) == 0) {
            throw new BusinessException("该原料不存在,请联系管理员添加字典");
        }
        //验证单位 入库单位必须是kg
        if (materialInboundRecord.getUnit().equals("吨")) {
            materialInboundRecord.setQuantity(materialInboundRecord.getQuantity() * 1000);
            materialInboundRecord.setUnitPrice(materialInboundRecord.getUnitPrice().divide(new BigDecimal(1000)));
            materialInboundRecord.setUnit("千克");
        }
        //验证税率 税费
        if (materialInboundRecord.getUnitPrice().multiply(new BigDecimal(materialInboundRecord.getQuantity())).compareTo(materialInboundRecord.getTotalPrice()) != 0) {
            throw new BusinessException("含税总金额计算错误 请检查并重新提交");
        }
        //如果已入库
        if (materialInboundRecord.getInboundTime() != null) {
            materialInboundRecord.setStatus("已入库");
            materialInboundRecord.setMaterialLeft(materialInboundRecord.getQuantity());
        }
        {
            materialInboundRecord.setStatus("未入库");
        }
        materialInboundRecord.setCreatedTime(LocalDateTime.now());
        materialInboundRecordMapper.insert(materialInboundRecord);
        return ResultResponse.success();
    }


    @Override
    public ResultResponse detail(UserVo userVo, Integer id) {
        MaterialInboundRecord old = materialInboundRecordMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        MaterialInboundRecordResponse response = new MaterialInboundRecordResponse();
        response.setSupplierName(supplierInfoMapper.selectById(old.getSupplierId()).getSupplierName());
        BeanUtils.copyProperties(old, response);
        return ResultResponse.success(response);
    }

    @Override
    public ResultResponse list(UserVo userVo, MaterialInboundRecordPageRequest request) {
        List<MaterialInboundRecordResponse> responseList = materialInboundRecordMapper.list(request);
        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), responseList);
        return ResultResponse.success(result);
    }

    @Override
    public ResultResponse inbound(UserVo userVo, Integer id) {
        MaterialInboundRecord old = materialInboundRecordMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        old.setInboundTime(LocalDateTime.now());
        old.setMaterialLeft(old.getQuantity());
        old.setStatus("已入库");
        materialInboundRecordMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse delete(UserVo userVo, Integer id) {
        MaterialInboundRecord old = materialInboundRecordMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        if (old.getStatus().equals("用尽")) {
            throw new BusinessException("该记录的原料未使用完,不可删除");
        }
        old.setDeletedTime(LocalDateTime.now());
        materialInboundRecordMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse total(UserVo userVo, MaterialInboundRecordPageRequest request) {
        //获取同一原料名 通同一供货商的记录
        List<MaterialInboundRecordResponse> list = materialInboundRecordMapper.totalList(request);

        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }
}
