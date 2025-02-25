package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.MaterialInboundRecordPageRequest;
import com.mortal.wms.business.entity.MaterialInboundRecord;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;

public interface MaterialInboundRecordService extends IService<MaterialInboundRecord> {
    ResultResponse add(UserVo userVo, MaterialInboundRecord material);

    ResultResponse detail(UserVo userVo, Integer id);

    ResultResponse list(UserVo userVo, MaterialInboundRecordPageRequest request);

    ResultResponse inbound(UserVo userVo, Integer id);

    ResultResponse delete(UserVo userVo, Integer id);

    ResultResponse total(UserVo userVo, MaterialInboundRecordPageRequest request);
}
