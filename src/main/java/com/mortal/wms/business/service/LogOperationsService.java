package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.dto.LogOperationsRequest;
import com.mortal.wms.business.entity.LogOperations;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;

public interface LogOperationsService extends IService<LogOperations> {

    ResultResponse addLogOperations(@CurrentUser UserVo userVo, LogOperations logOperationsEntity);

    ResultResponse<IPage<LogOperations>> select(LogOperationsRequest request);
}
