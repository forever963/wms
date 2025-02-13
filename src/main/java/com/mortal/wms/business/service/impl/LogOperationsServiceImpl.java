package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.LogOperationsRequest;
import com.mortal.wms.business.entity.LogOperations;
import com.mortal.wms.business.mapper.LogOperationsMapper;
import com.mortal.wms.business.service.LogOperationsService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogOperationsServiceImpl extends ServiceImpl<LogOperationsMapper, LogOperations> implements LogOperationsService {

    @Autowired
    private LogOperationsMapper logOperationsMapper;

    @Override
    public ResultResponse addLogOperations(UserVo userVo, LogOperations log) {

        log.setUserId(1L);
        log.setUserAgent(userVo.getUserAgent());
        log.setIp(userVo.getIp());
        int insert = logOperationsMapper.insert(log);
        if (insert > 0) {
            ResultResponse.success(log);
        }
        return ResultResponse.error();
    }


    @Override
    public ResultResponse<IPage<LogOperations>> select(LogOperationsRequest request) {
        IPage<LogOperations> iPage = new Page<>(request.getPageNum(), request.getPageSize());

        IPage<LogOperations> iPage1 = logOperationsMapper.select(iPage, request);

        return ResultResponse.success(iPage1);
    }
}
