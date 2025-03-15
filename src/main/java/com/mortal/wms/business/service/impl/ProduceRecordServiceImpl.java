package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.ProduceRecordRequest;
import com.mortal.wms.business.dto.ProduceMaterialPageRequest;
import com.mortal.wms.business.dto.ProduceRecordResponse;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        request.setTotalCost(BigDecimal.ZERO);
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
                throw new BusinessException(x.getMaterialName() + "原料不足");
            }
            //原料入库记录表需要做出相应修改
            //用id 查出该原料的入库记录 修改它的剩余
            MaterialInboundRecord m = materialInboundRecordMapper.selectById(x.getMaterialInboundRecordId());

            m.setMaterialLeft(m.getMaterialLeft() - x.getQuantityUsed());
            //删减库存后 修改状态
            if (m.getMaterialLeft() > 0) {
                m.setStatus("使用中");
            } else {
                m.setStatus("用尽");
            }
            //更新原料入库记录表的库存
            materialInboundRecordMapper.updateById(m);
            //记录成本
            request.setTotalCost(request.getTotalCost().add(m.getUnitPrice().multiply(new BigDecimal(x.getQuantityUsed()))));
            //原料使用记录表
            produceMaterialMapper.insert(x);
        });
//        //插入生产记录表
        produceRecordMapper.updateById(request);

        return ResultResponse.success();
    }

    @Override
    public ResultResponse produceRecordList(UserVo userVo, ProductPageRequest request) {
        List<ProductMaterialResponse> materialResponses = produceMaterialMapper.list(null);
        List<ProduceRecord> list = produceRecordMapper.list(request);
        if(request.getPageNum()==null && request.getPageSize()==null && request.getProductName()!=null){
            list = list.stream().filter(x->x.getLeftQuantity()!=0).collect(Collectors.toList());
            return ResultResponse.success(list);
        }
        List<ProduceRecordResponse> responses = new ArrayList<>();
        list.stream().forEach(x -> {
            ProduceRecordResponse response = new ProduceRecordResponse();
            List<ProductMaterialResponse> productMaterialResponseList = new ArrayList<>();
            BeanUtils.copyProperties(x, response);
            materialResponses.stream().forEach(m -> {
                if (Objects.equals(x.getId(), m.getProduceRecordId())) {
                    productMaterialResponseList.add(m);
                }
            });
            response.setProductMaterialResponseList(productMaterialResponseList);
            responses.add(response);
        });

        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), responses);
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