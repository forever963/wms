package com.mortal.wms.business.service.impl;

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
        List<InfoCategories> list = infoCategoriesMapper.list(request.getType());
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }
}
