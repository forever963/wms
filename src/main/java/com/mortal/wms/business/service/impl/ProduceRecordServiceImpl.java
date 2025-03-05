package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.ProduceRecordRequest;
import com.mortal.wms.business.dto.ProduceMaterialPageRequest;
import com.mortal.wms.business.dto.ProductPageRequest;
import com.mortal.wms.business.entity.MaterialInboundRecord;
import com.mortal.wms.business.entity.ProduceMaterial;
import com.mortal.wms.business.entity.ProduceRecord;
import com.mortal.wms.business.mapper.*;
import com.mortal.wms.business.service.ProduceRecordService;
import com.mortal.wms.business.vo.ProductMaterialResponse;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProduceRecordServiceImpl extends ServiceImpl<ProduceRecordMapper, ProduceRecord> implements ProduceRecordService {
    @Autowired
    private ProduceRecordMapper produceRecordMapper;
    @Autowired
    private ProduceMaterialMapper produceMaterialMapper;
    @Autowired
    private MaterialInboundRecordMapper materialInboundRecordMapper;

    @Override
    public ResultResponse add(UserVo userVo, ProduceRecordRequest request) {
        //验证单位 入库单位必须是KG
        if (request.getUnit().equals("T")) {
            request.setProduceQuantity(request.getProduceQuantity() * 1000);
            request.setUnit("KG");
        }
        //同步到剩余库存 为了订单出库
        request.setLeftQuantity(request.getProduceQuantity());
        request.setCreatedTime(LocalDateTime.now());
        //插入 拿到主键id
        produceRecordMapper.insert(request);
        int insert = request.getId();
        //检查原料消耗 并写入原料消耗记录
        request.getProduceMaterialList().stream().forEach(x -> {
            if (x.getUnit().equals("T")) {
                x.setQuantityUsed(x.getQuantityUsed() * 1000);
                x.setUnit("KG");
            }
            //生产总成本 = 各次 原料使用成本相加
            x.setProduceRecordId(insert);
            x.setCreatedTime(LocalDateTime.now());

            int totalQuantity = materialInboundRecordMapper.getTotalQuantity(x.getMaterialName(), x.getSupplierId());
            if (totalQuantity < x.getQuantityUsed()) {
                throw new BusinessException(x.getMaterialName()+"原料不足");
            }
            //原料入库记录表需要做出相应修改
            //用name 查出该原料 1.未使用完 2.最早一次入库 的记录 TODO
            List<MaterialInboundRecord> materialInboundRecords = materialInboundRecordMapper.selectList(new LambdaQueryWrapper<MaterialInboundRecord>()
                    .eq(MaterialInboundRecord::getMaterialName, x.getMaterialName())
                    .eq(MaterialInboundRecord::getSupplierId, x.getSupplierId())
                    .ge(MaterialInboundRecord::getMaterialLeft, 0)
                    .orderByAsc(MaterialInboundRecord::getInboundTime)
                    .isNull(MaterialInboundRecord::getDeletedTime)
            );
            for (MaterialInboundRecord y : materialInboundRecords) {
                ProduceMaterial produceMaterial = new ProduceMaterial();
                produceMaterial.setTotalCost(new BigDecimal("0"));
                produceMaterial.setMaterialName(x.getMaterialName());
                produceMaterial.setSupplierId(x.getSupplierId());
                produceMaterial.setProduceRecordId(insert);
                produceMaterial.setCreatedTime(LocalDateTime.now());


                //该次入库记录够本次生产的消耗
                if (y.getMaterialLeft() >= x.getQuantityUsed()) {
                    produceMaterial.setMaterialInboundRecordId(y.getId());
                    produceMaterial.setUnit(y.getUnit());
                    produceMaterial.setTotalCost(produceMaterial.getTotalCost().add(y.getUnitPrice().multiply(new BigDecimal(x.getQuantityUsed()))));
                    produceMaterial.setCostPerUnit(y.getUnitPrice());
                    produceMaterial.setQuantityUsed(x.getQuantityUsed());
                    //原料消耗
                    y.setMaterialLeft(y.getMaterialLeft() - x.getQuantityUsed());
                    if (y.getMaterialLeft() > 0) {
                        y.setStatus("使用中");
                    } else {
                        y.setStatus("用尽");
                    }
                    //原料记录表消耗
                    materialInboundRecordMapper.updateById(y);
                    //记录成本
                    request.setTotalCost(request.getTotalCost().add(produceMaterial.getTotalCost()));
                    //原料使用记录表
                    produceMaterialMapper.insert(produceMaterial);
                    break;
                } else {
                    //不够消耗 则该次入库记录剩余归零 进下一次循环
                    x.setQuantityUsed(x.getQuantityUsed() - y.getMaterialLeft());
                    produceMaterial.setMaterialInboundRecordId(y.getId());
                    produceMaterial.setUnit(y.getUnit());
                    produceMaterial.setCostPerUnit(y.getUnitPrice());
                    produceMaterial.setQuantityUsed(y.getQuantity());
                    produceMaterial.setTotalCost(produceMaterial.getTotalCost().add(y.getUnitPrice().multiply(BigDecimal.valueOf(y.getQuantity()))));
                    y.setMaterialLeft(0);
                    y.setStatus("用尽");
                    //原料记录表消耗
                    materialInboundRecordMapper.updateById(y);
                    //记录成本
                    request.setTotalCost(request.getTotalCost().add(produceMaterial.getTotalCost()));
                    //原料使用记录表
                    produceMaterialMapper.insert(produceMaterial);
                }

            }
        });
//        //插入生产记录表
        produceRecordMapper.updateById(request);

        return ResultResponse.success();
    }

    @Override
    public ResultResponse produceRecordList(UserVo userVo, ProductPageRequest request) {
        List<ProduceRecord> list = produceRecordMapper.list();
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }

    @Override
    public ResultResponse produceMaterialList(UserVo userVo, ProduceMaterialPageRequest request) {
        List<ProductMaterialResponse> list = produceMaterialMapper.list(request);
        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(result);
    }

    @Override
    public ResultResponse totalList(UserVo userVo, ProductPageRequest request) {
        List<ProduceRecord> list = produceRecordMapper.total();
        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(result);
    }
}