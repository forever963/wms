package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.entity.InfoCategories;

import java.util.List;

public interface InfoCategoriesService extends IService<InfoCategories> {
    List<String> getNamesByType(Integer type);

    Boolean ifExists(String name);
}
