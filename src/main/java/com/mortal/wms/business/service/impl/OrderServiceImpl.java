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
                if (y.getOrderId().equals(x.getId())) {
                    totalPrice = totalPrice.add(y.getUnitPrice().multiply(new BigDecimal(y.getQuantity())));
                }
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
        Orders num = orderMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .isNull(Orders::getDeletedTime)
                .eq(Orders::getOrderNum, request.getOrderNum())
        );
        if(num != null) {
            return ResultResponse.error("合同编号已存在");
        }
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
            x.setCreatedTime(request.getOrderCreationTime());
            x.setOrderId(orders.getId());
        });
        //批量插入
        orderProductMapper.insert(request.getOrderProductList());
        return ResultResponse.success();
    }

    @Override
    public ResultResponse receipt(UserVo userVo, OrderReceipt request) {
        if (request.getReceiptTime() == null) {
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
        if (request.getOutboundTime() == null) {
            request.setOutboundTime(LocalDateTime.now());
        }
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

        PageResult result = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(result);
    }

    @Override
    public ResultResponse homeData(Integer year) {
        HomeDataVo homeDataVo = new HomeDataVo();

        // 设置年度订单总额，如果为null则设置为0
        BigDecimal annualOrderTotal = orderProductMapper.getAnnualOrderTotal(year);
        homeDataVo.setAnnualOrderTotal(annualOrderTotal != null ? annualOrderTotal.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // 设置年度补货总额，如果为null则设置为0
        BigDecimal restockAnnualTotal = materialInboundRecordMapper.getTotal(year);
        homeDataVo.setRestockAnnualTotal(restockAnnualTotal != null ? restockAnnualTotal.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // 设置客户数量，如果为null则设置为0
        Integer customerNumber = customerInfoMapper.selectCount(new LambdaQueryWrapper<CustomerInfo>().isNull(CustomerInfo::getDeletedTime)).intValue();
        homeDataVo.setCustomerNumber(customerNumber != null ? customerNumber : 0);

        // 设置供应商数量，如果为null则设置为0
        Integer supplierNumber = supplierInfoMapper.selectCount(new LambdaQueryWrapper<SupplierInfo>().isNull(SupplierInfo::getDeletedTime)).intValue();
        homeDataVo.setSupplierNumber(supplierNumber != null ? supplierNumber : 0);

        // 设置月度订单总额，如果为null则设置为空Map
        Map<Integer, BigDecimal> map1 = new HashMap<>();
        List<OrderProduct> orderProducts = orderProductMapper.selectList(new LambdaQueryWrapper<OrderProduct>().isNull(OrderProduct::getDeletedTime).apply("YEAR(created_time) = {0}", year));
        if (orderProducts != null) {
            orderProducts.forEach(x -> {
                int month = x.getCreatedTime().getMonth().getValue();
                BigDecimal currentProductTotalPrice = x.getUnitPrice().multiply(BigDecimal.valueOf(x.getQuantity()));
                map1.merge(month, currentProductTotalPrice, BigDecimal::add);
            });
        }
        homeDataVo.setMonthlyOrderTotal(map1);

        homeDataVo.setIncomeTotal(BigDecimal.ZERO);
        // 设置月度收入，如果为null则设置为空Map
        Map<Integer, BigDecimal> map2 = new HashMap<>();
        List<OrderReceipt> receipts = orderReceiptMapper.selectList(new LambdaQueryWrapper<OrderReceipt>().isNull(OrderReceipt::getDeletedTime).apply("YEAR(receipt_time) = {0}", year));
        if (receipts != null) {
            receipts.forEach(x -> {
                int month = x.getReceiptTime().getMonth().getValue();
                BigDecimal currentProductTotalPrice = x.getAmountReceived();
                homeDataVo.setIncomeTotal(homeDataVo.getIncomeTotal().add(currentProductTotalPrice));
                map2.merge(month, currentProductTotalPrice, BigDecimal::add);
            });
        }
        homeDataVo.setIncome(map2);



        Map<Integer, BigDecimal> map3 = new HashMap<>();
        // 设置月度支出，如果为null则设置为空Map
        List<MaterialInboundRecord> materialInboundRecords = materialInboundRecordMapper.selectList(new LambdaQueryWrapper<MaterialInboundRecord>()
                .isNull(MaterialInboundRecord::getDeletedTime).apply("YEAR(order_initiated_time) = {0}", year));

        homeDataVo.setExpenseTotal(BigDecimal.ZERO);
        if (materialInboundRecords != null) {
            materialInboundRecords.forEach(x -> {
                int month = x.getInboundTime().getMonth().getValue();
                BigDecimal currentProductTotalPrice = x.getTotalPrice();
                homeDataVo.setExpenseTotal(homeDataVo.getExpenseTotal().add(currentProductTotalPrice));
                map3.merge(month, currentProductTotalPrice, BigDecimal::add);
            });
        }

        homeDataVo.setExpense(map3);

        return ResultResponse.success(homeDataVo);
    }
}