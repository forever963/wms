package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.*;
import com.mortal.wms.business.entity.Orders;
import com.mortal.wms.business.entity.OrderReceipt;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface OrderService extends IService<Orders> {

    ResultResponse deleteOrder(UserVo userVo, Integer id);

    ResultResponse getOrderById(UserVo userVo, Integer id);

    ResultResponse listOrders(UserVo userVo, OrderPageRequest request);

    ResultResponse addOrder(UserVo userVo, OrdersRequest request);

    ResultResponse receipt(UserVo userVo, OrderReceipt request);

    ResultResponse outbound(UserVo userVo, OrderOutBoundRequest request);

    ResultResponse outBoundRecordlist(UserVo userVo, OrderOutBoundPageRequest request);

    ResultResponse homeData(Integer year);

    ResultResponse owe(UserVo userVo, OweOrderRequest request);

    void exportContract(HttpServletResponse response, ContractData data)throws IOException;
}