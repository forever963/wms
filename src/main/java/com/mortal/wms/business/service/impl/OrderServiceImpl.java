package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.OrderOutBoundPageRequest;
import com.mortal.wms.business.dto.OrderOutBoundRequest;
import com.mortal.wms.business.dto.OrderPageRequest;
import com.mortal.wms.business.dto.OrdersRequest;
import com.mortal.wms.business.entity.*;
import com.mortal.wms.business.mapper.*;
import com.mortal.wms.business.service.InfoCategoriesService;
import com.mortal.wms.business.service.OrderService;
import com.mortal.wms.business.vo.HomeDataVo;
import com.mortal.wms.business.vo.OrdersResponse;
import com.mortal.wms.business.vo.ProductOutboundRecordResponse;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderProductMapper orderProductMapper;
    @Autowired
    private CustomerInfoMapper customerInfoMapper;
    @Autowired
    private OrderReceiptMapper orderReceiptMapper;
    @Autowired
    private InfoCategoriesService infoCategoriesService;
    @Autowired
    private ProductOutboundRecordMapper productOutboundRecordMapper;
    @Autowired
    private ProduceRecordMapper produceRecordMapper;
    @Autowired
    private SupplierInfoMapper supplierInfoMapper;
    @Autowired
    private MaterialInboundRecordMapper materialInboundRecordMapper;


    @Override
    public ResultResponse deleteOrder(UserVo userVo, Integer id) {
        Orders old = orderMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        old.setDeletedTime(LocalDateTime.now());
        List<OrderProduct> orderProducts = orderProductMapper.selectList(new LambdaQueryWrapper<OrderProduct>().eq(OrderProduct::getOrderId, id).isNull(OrderProduct::getDeletedTime));
        orderProducts.forEach(x -> x.setDeletedTime(LocalDateTime.now()));
        List<OrderReceipt> orderReceipts = orderReceiptMapper.selectList(new LambdaQueryWrapper<OrderReceipt>().eq(OrderReceipt::getOrderId, id).isNull(OrderReceipt::getDeletedTime));
        if (orderReceipts != null && !orderReceipts.isEmpty()) {
            orderReceipts.forEach(x -> x.setDeletedTime(LocalDateTime.now()));
        }
        orderReceiptMapper.updateById(orderReceipts);
        orderProductMapper.updateById(orderProducts);
        orderMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse getOrderById(UserVo userVo, Integer id) {
        Orders old = orderMapper.selectById(id);
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        OrdersResponse orderResponse = new OrdersResponse();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BeanUtils.copyProperties(old, orderResponse);
        orderResponse.setCustomerInfoName(customerInfoMapper.selectById(orderResponse.getCustomerInfoId()).getCompanyName());
        orderResponse.setOrderProductList(orderProductMapper.selectList(new LambdaQueryWrapper<OrderProduct>()
                .eq(OrderProduct::getOrderId, id)
                .isNull(OrderProduct::getDeletedTime)
        ));
        for (OrderProduct x : orderResponse.getOrderProductList()) {
            totalPrice = totalPrice.add(x.getUnitPrice().multiply(new BigDecimal(x.getQuantity())));
        }
        orderResponse.setTotalPrice(totalPrice);
        return ResultResponse.success(orderResponse);
    }

    @Override
    public ResultResponse listOrders(UserVo userVo, OrderPageRequest request) {
        List<OrdersResponse> list = orderMapper.list(request);
        Map<Integer, CustomerInfo> customerInfosMap = customerInfoMapper.selectList(new LambdaQueryWrapper<CustomerInfo>().isNull(CustomerInfo::getDeletedTime)).stream().collect(Collectors.toMap(CustomerInfo::getId, x -> x));
        List<OrderProduct> orderProducts = orderProductMapper.selectList(new LambdaQueryWrapper<OrderProduct>().isNull(OrderProduct::getDeletedTime));
        Map<Integer, List<OrderProduct>> map = orderProducts.stream().collect(Collectors.groupingBy(OrderProduct::getOrderId));
        list.forEach(x -> {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (OrderProduct y : orderProducts) {
                totalPrice = totalPrice.add(y.getUnitPrice().multiply(new BigDecimal(y.getQuantity())));
            }
            x.setCustomerInfoName(customerInfosMap.get(x.getCustomerInfoId()).getCompanyName());
            x.setOrderProductList(map.get(x.getId()));
            x.setTotalPrice(totalPrice);
        });
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }

    @Override
    public ResultResponse addOrder(UserVo userVo, OrdersRequest request) {
        //验证产品名是否在字典中
        List<String> namesByType = infoCategoriesService.getNamesByType(3);
        Orders orders = new Orders();
        BeanUtils.copyProperties(request, orders);
        orders.setCreatedTime(LocalDateTime.now());
        orderMapper.insert(orders);
        request.getOrderProductList().forEach(x -> {
            if (!namesByType.contains(x.getProductName())) {
                throw new BusinessException(x.getProductName() + "该产品名不存在 请添加字典");
            }
            x.setCreatedTime(LocalDateTime.now());
            x.setOrderId(orders.getId());
        });
        //批量插入
        orderProductMapper.insert(request.getOrderProductList());
        return ResultResponse.success();
    }

    @Override
    public ResultResponse receipt(UserVo userVo, OrderReceipt request) {
        if(request.getReceiptTime()==null){
            request.setReceiptTime(LocalDateTime.now());
        }
        orderReceiptMapper.insert(request);
        Orders old = orderMapper.selectById(request.getOrderId());
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        //修改订单表
        old.setPaidAmount(old.getPaidAmount().add(request.getAmountReceived()));
        //验证插入的收款记录是否正确
        BigDecimal total = orderProductMapper.getTotalByOrderId(request.getOrderId());
        if (total.compareTo(old.getPaidAmount()) < 0) {
            throw new BusinessException("当前收款金额大于订单总金额,请重新提交");
        }
        orderMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse outbound(UserVo userVo, OrderOutBoundRequest request) {
        //订单产品
        OrderProduct old = orderProductMapper.selectById(request.getOrderProductId());

        List<ProductOutboundRecord> list = new ArrayList<>();
        //
        Map<Integer, ProduceRecord> produceRecordMap = produceRecordMapper.selectList(new LambdaQueryWrapper<ProduceRecord>()
                        .isNull(ProduceRecord::getDeletedTime)
                        .gt(ProduceRecord::getLeftQuantity, 0))
                .stream().collect(Collectors.toMap(ProduceRecord::getId, x -> x));
        request.getOutBoundProductList().forEach(x -> {
            old.setOutboundQuantity(old.getOutboundQuantity() + x.getQuantity());
            if (old.getOutboundQuantity() > old.getQuantity()) {
                throw new BusinessException("出库数量异常");
            }
            ProductOutboundRecord p = new ProductOutboundRecord();
            BeanUtils.copyProperties(x, p);
            //验证单位
            if (x.getUnit().equals("T")) {
                x.setUnit("KG");
                x.setQuantity(x.getQuantity() * 1000);
            }
            p.setOrderProductId(request.getOrderProductId());
            p.setCreatedTime(LocalDateTime.now());
            p.setOutboundTime(request.getOutboundTime());
            //修改产品入库的库存
            produceRecordMap.get(p.getProduceRecordId()).setLeftQuantity(
                    produceRecordMap.get(p.getProduceRecordId()).getLeftQuantity() - x.getQuantity()
            );
            //验证库存
            if (produceRecordMap.get(p.getProduceRecordId()).getProduceQuantity() < 0) {
                throw new BusinessException(produceRecordMap.get(p.getProduceRecordId()).getProductName() + "生产日期:" + produceRecordMap.get(p.getProduceRecordId()).getCreatedTime() + "库存不足");
            }
            list.add(p);
        });
        // 更新库存记录
        for (ProduceRecord produceRecord : produceRecordMap.values()) {
            produceRecordMapper.updateById(produceRecord);
        }
        productOutboundRecordMapper.insert(list);
        orderProductMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse outBoundRecordlist(UserVo userVo, OrderOutBoundPageRequest request) {
        List<ProductOutboundRecordResponse> list = productOutboundRecordMapper.list(request);

        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(),request.getPageSize(),list);
        return ResultResponse.success(result);
    }

    @Override
    public ResultResponse homeData(Integer year) {
        HomeDataVo homeDataVo = new HomeDataVo();

        homeDataVo.setAnnualOrderTotal(orderProductMapper.getAnnualOrderTotal(year).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
        homeDataVo.setRestockAnnualTotal(materialInboundRecordMapper.getTotal(year).divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP));
        homeDataVo.setCustomerNumber(customerInfoMapper.selectCount(new LambdaQueryWrapper<CustomerInfo>().isNull(CustomerInfo::getDeletedTime)).intValue());
        homeDataVo.setSupplierNumber(supplierInfoMapper.selectCount(new LambdaQueryWrapper<SupplierInfo>().isNull(SupplierInfo::getDeletedTime)).intValue());

        Map<Integer,BigDecimal> map1 = new HashMap<>();
        List<OrderProduct> orderProducts = orderProductMapper.selectList(new LambdaQueryWrapper<OrderProduct>().isNull(OrderProduct::getDeletedTime).apply("YEAR(created_time) = {0}", year));
        orderProducts.forEach(x -> {
            int month = x.getCreatedTime().getMonth().getValue();
            BigDecimal currentProductTotalPrice = x.getUnitPrice().multiply(BigDecimal.valueOf(x.getQuantity()));
            map1.merge(month, currentProductTotalPrice, BigDecimal::add);
        });
        homeDataVo.setMonthlyOrderTotal(map1);

        Map<Integer,BigDecimal> map2 = new HashMap<>();
        List<Orders> ordersList = orderMapper.selectList(new LambdaQueryWrapper<Orders>().isNull(Orders::getDeletedTime).apply("YEAR(order_creation_time) = {0}", year));
        ordersList.forEach(x -> {
            int month = x.getCreatedTime().getMonth().getValue();
            BigDecimal currentProductTotalPrice = x.getPaidAmount();
            map2.merge(month, currentProductTotalPrice, BigDecimal::add);
        });
        homeDataVo.setIncome(map2);
        List<MaterialInboundRecord> materialInboundRecords = materialInboundRecordMapper.selectList(new LambdaQueryWrapper<MaterialInboundRecord>()
                .isNull(MaterialInboundRecord::getDeletedTime).apply("YEAR(order_initiated_time) = {0}", year));
        Map<Integer,BigDecimal> map3 = new HashMap<>();

        materialInboundRecords.forEach(x -> {
            int month = x.getOrderInitiatedTime().getMonth().getValue();
            BigDecimal currentProductTotalPrice = x.getTotalPrice();
            map3.merge(month, currentProductTotalPrice, BigDecimal::add);
        });

        homeDataVo.setExpense(map3);
        return ResultResponse.success(homeDataVo);
    }
}