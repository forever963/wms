package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.*;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        if(request.getPageNum()==null || request.getPageNum()==0){
            return ResultResponse.success(list);
        }
        PageResult pageResult = PageResult.ckptPageUtilList(request.getPageNum(), request.getPageSize(), list);
        return ResultResponse.success(pageResult);
    }

    @Override
    public ResultResponse addOrder(UserVo userVo, OrdersRequest request) {
        Orders num = orderMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .isNull(Orders::getDeletedTime)
                .eq(Orders::getOrderNum, request.getOrderNum())
        );
        if (num != null) {
            throw new BusinessException("合同编号已存在");
        }
        //验证产品名是否在字典中
        List<String> namesByType = infoCategoriesService.getNamesByType(3);
        Orders orders = new Orders();
        BeanUtils.copyProperties(request, orders);
        orders.setCreatedTime(LocalDateTime.now());
        orderMapper.insert(orders);
        //准备出库记录
        List<ProductOutboundRecord> productOutboundRecordList = new ArrayList<>();
        //遍历订单的货物列表 产品名称校验 删减生产记录库存
        request.getOrderProductList().forEach(x -> {
            if (!namesByType.contains(x.getProductName())) {
                throw new BusinessException(x.getProductName() + "该产品名不存在 请添加字典");
            }
            //这里 删减生产记录的库存 并整理出库记录数据
            //查找出对应产品的生产记录  产品名称=当前  剩余库存>0 没有被删除 按剩余库存逆序
            List<ProduceRecord> produceRecordList = produceRecordMapper.selectList(new LambdaQueryWrapper<ProduceRecord>().
                    eq(ProduceRecord::getProductName, x.getProductName())
                    .gt(ProduceRecord::getLeftQuantity, 0)
                    .isNull(ProduceRecord::getDeletedTime)
                    .orderByDesc(ProduceRecord::getLeftQuantity)
            );
            for (ProduceRecord y : produceRecordList) {
                ProductOutboundRecord productOutboundRecord = new ProductOutboundRecord();
                productOutboundRecord.setOutboundTime(orders.getOrderCreationTime());
                productOutboundRecord.setUnit("KG");
                productOutboundRecord.setProduceRecordId(y.getId());
                productOutboundRecord.setOrderProductId(orders.getId());
                //如果当前生产记录大于等于 需要消耗的数量 则直接记录
                if (y.getLeftQuantity() >= x.getQuantity()) {
                    //1.扣减库存
                    y.setLeftQuantity(y.getLeftQuantity() - x.getQuantity());
                     //扣减后入库
                    produceRecordMapper.updateById(y);
                    //2.整理 出库记录 数据
                    productOutboundRecord.setQuantity(x.getQuantity());
                    //3.写入记录
                    productOutboundRecordList.add(productOutboundRecord);
                    break;
                }
                //当前生产记录小于需求 则需要进下一次循环
                //整理出库记录数据
                productOutboundRecord.setQuantity(x.getQuantity()-y.getLeftQuantity());
                 //扣减生产记录为0
                y.setLeftQuantity(0);
                 //扣减后入库
                produceRecordMapper.updateById(y);
                // 更新还需要的数量
                x.setQuantity(x.getQuantity() - y.getLeftQuantity());
            }
            //将订单产品表 的 创建时间 同步为 订单创建时间
            x.setCreatedTime(request.getOrderCreationTime().atStartOfDay());
            x.setOrderId(orders.getId());
        });
        //写入出库记录 批量插入
        productOutboundRecordMapper.insert(productOutboundRecordList);
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
        }else if(total.compareTo(old.getPaidAmount()) == 0){//结清
            old.setOwe(true);
        }
        orderMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse outbound(UserVo userVo, OrderOutBoundRequest request) {
        //订单产品
        OrderProduct old = orderProductMapper.selectById(request.getOrderProductId());
        if (request.getOutboundTime() == null) {
            request.setOutboundTime(LocalDate.now());
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

    @Override
    public ResultResponse owe(UserVo userVo, OweOrderRequest request) {
        List<OrdersRequest> ordersRequestList = new ArrayList<>();
        List<OrdersResponse> oweOrders = orderMapper.getOweOrder(request);
        

        return ResultResponse.success(ordersRequestList);
    }

    @Override
    public void exportContract(HttpServletResponse response, ContractData data) throws IOException {
        //这里模板会爆莫名其妙的错误 提示模板损坏 换个模板名字就好
        // 1. 设置头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "";
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
        InputStream templateStream = null;
        OutputStream out = null;
        try {
            // 2. 读模板
            ClassPathResource resource = new ClassPathResource("templates/c.xlsx");
            if (!resource.exists()) throw new FileNotFoundException("模板不存在");
            templateStream = resource.getInputStream();
            // 3. 获取输出流
            out = response.getOutputStream();
            // 4. 准备数据
            Context context = new Context();
            // —— 合同基本信息 ——
            context.putVar("contractNumber", data.getContractNumber());
            context.putVar("buyerName", data.getBuyerName());
            context.putVar("deliveryAddress", data.getDeliveryAddress());
            context.putVar("contactPerson", data.getContactPerson());
            context.putVar("contactPhone", data.getContactPhone());
            context.putVar("paymentMethod", data.getPaymentMethod());
            context.putVar("deliveryDate", data.getDeliveryDate());
            context.putVar("contractExpiryDate", data.getContractExpiryDate());
            // 确保模板中使用 jx:each items="productList" var="item"
            context.putVar("productList", data.getProductList());
            context.putVar("totalAmount", data.getTotalAmount());
            context.putVar("totalAmountInWords", data.getTotalAmountInWords());
            context.putVar("remark", data.getRemark());
            context.putVar("nowDate", data.getNowDate());
            JxlsHelper.getInstance()
                    .setEvaluateFormulas(true)
                    .setUseFastFormulaProcessor(false) // 尝试添加这一行，禁用快速处理器，使用标准解析器
                    .processTemplate(templateStream, out, context);
            // 6. 刷新
            out.flush();
        } catch (Exception e) {
            log.error("导出过程中发生异常", e);
            if (!response.isCommitted()) {
                response.reset();
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("导出失败：" + e.getMessage());
            }
            throw new IOException("导出失败", e);
        } finally {
            if (templateStream != null) {
                try { templateStream.close(); } catch (IOException ignored) {}
            }
            //绝对不要 close(out)
        }
    }

}