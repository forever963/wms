package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.ProduceRecordRequest;
import com.mortal.wms.business.dto.ProduceMaterialPageRequest;
import com.mortal.wms.business.dto.ProductPageRequest;
import com.mortal.wms.business.entity.ProduceRecord;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;


public interface ProduceRecordService extends IService<ProduceRecord> {

    ResultResponse add(UserVo userVo, ProduceRecordRequest request);

    ResultResponse produceRecordList(UserVo userVo, ProductPageRequest request);

    ResultResponse produceMaterialList(UserVo userVo, ProduceMaterialPageRequest request);

    ResultResponse totalList(UserVo userVo, ProductPageRequest request);
}
