package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.MaterialPageRequest;
import com.mortal.wms.business.entity.Material;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;

public interface MaterialService extends IService<Material> {
    ResultResponse add(UserVo userVo, Material material);

    ResultResponse detail(UserVo userVo, Integer id);

    ResultResponse list(UserVo userVo, MaterialPageRequest request);

    ResultResponse inbound(UserVo userVo, Integer id);

    ResultResponse delete(UserVo userVo, Integer id);

    ResultResponse total(UserVo userVo, MaterialPageRequest request);
}
