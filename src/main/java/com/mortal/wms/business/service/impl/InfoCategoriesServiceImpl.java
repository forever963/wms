package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.InfoCategoriesRequest;
import com.mortal.wms.business.entity.InfoCategories;
import com.mortal.wms.business.mapper.InfoCategoriesMapper;
import com.mortal.wms.business.service.InfoCategoriesService;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InfoCategoriesServiceImpl extends ServiceImpl<InfoCategoriesMapper, InfoCategories> implements InfoCategoriesService {
    @Autowired
    private InfoCategoriesMapper infoCategoriesMapper;

    @Override
    public List<String> getNamesByType(Integer type) {
        List<String> list = infoCategoriesMapper.getNamesByType(type);
        return list;
    }

    @Override
    public Boolean ifExists(String name) {
        return infoCategoriesMapper.get(name) != 0;
    }

    @Override
    public ResultResponse list(InfoCategoriesRequest request) {
        if(request.getPageNum() == null && request.getType() != null) {
            List<InfoCategories> list = infoCategoriesMapper.list(request.getType());
            return ResultResponse.success(list);
        }
        List<InfoCategories> list = infoCategoriesMapper.list(request.getType());
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }

    @Override
    public ResultResponse add(InfoCategories request) {
        InfoCategories infoCategories = infoCategoriesMapper.selectOne(new LambdaQueryWrapper<InfoCategories>()
                .eq(InfoCategories::getType, request.getType())
                .orderByDesc(InfoCategories::getDisplayOrder)
                .last("limit 1")
        );
        InfoCategories name = infoCategoriesMapper.selectOne(new LambdaQueryWrapper<InfoCategories>()
                .eq(InfoCategories::getName, request.getName())
        );
        if(name !=null){
            return ResultResponse.error("该字典名已存在");
        }
        request.setSearch(request.getName());
        //设置排序
        if(infoCategories == null) {
            request.setDisplayOrder(0);
        }else{
            request.setDisplayOrder(infoCategories.getDisplayOrder()+1);
        }
        infoCategoriesMapper.insert(request);
        return ResultResponse.success();
    }
}
