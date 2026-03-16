package com.mortal.wms.business.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.*;
import com.mortal.wms.business.entity.*;
import com.mortal.wms.business.mapper.*;
import com.mortal.wms.business.service.InfoCategoriesService;
import com.mortal.wms.business.service.OrderService;
import com.mortal.wms.business.vo.*;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.PageResult;
import com.mortal.wms.util.ResultResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.poi.ss.util.CellUtil.createCell;

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
        if (request.getIsOwe() != null && request.getIsOwe()) {
            OweOrderResponse orderResponse = new OweOrderResponse();
            //处理list  分成currentPeriodOrders  historyOrders
            List<OrdersResponse> currentPeriodOrders = new ArrayList<>();
            List<OrdersResponse> historyOrders = new ArrayList<>();
            //historyOrders的数据处理写入 monthlyBreakdown
            Map<YearMonth, BigDecimal> monthlyBreakdown = new LinkedHashMap<>();
            list.forEach(x -> {
                YearMonth ym = YearMonth.from(x.getOrderCreationTime());
                //  当月25号 包括25号的数据记入下个月
                if (x.getOrderCreationTime().getDayOfMonth() >= 25) {
                    ym = ym.plusMonths(1);
                }
                if (monthlyBreakdown.containsKey(ym)) {
                    monthlyBreakdown.put(ym, monthlyBreakdown.get(ym).add(x.getOwe()));
                } else {
                    monthlyBreakdown.put(ym, x.getOwe());
                }
                if (x.getOrderCreationTime() // [上个月25，这个月25)
                        .isBefore(request.getEndDate()) && x.getOrderCreationTime()
                        .isAfter(request.getEndDate().minusMonths(1).withDayOfMonth(24))) {
                    currentPeriodOrders.add(x);
                } else {
                    historyOrders.add(x);
                }
            });
            //将当月的数据找出 并弹出map
            BigDecimal currentTotalAmount = monthlyBreakdown.get(YearMonth.from(request.getEndDate()));
            monthlyBreakdown.remove(YearMonth.from(request.getEndDate()));
            currentPeriodOrders.sort(Comparator.comparing(OrdersResponse::getOrderCreationTime));
            historyOrders.sort(Comparator.comparing(OrdersResponse::getOrderCreationTime));
            orderResponse.setCurrentTotalAmount(currentTotalAmount);
            orderResponse.setMonthlyBreakdown(monthlyBreakdown);
            orderResponse.setCurrentPeriodOrders(currentPeriodOrders);
            orderResponse.setHistoryOrders(historyOrders);
            return ResultResponse.success(orderResponse);
        }
        if (request.getPageNum() == null || request.getPageNum() == 0) {
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
            //讲欠款细节计算加入数据
            x.setOweItem(x.getUnitPrice().multiply(new BigDecimal(x.getQuantity())));
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
                productOutboundRecord.setQuantity(x.getQuantity() - y.getLeftQuantity());
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
        BigDecimal remainingAmount = request.getAmountReceived();
        if (request.getReceiptTime() == null) {
            request.setReceiptTime(LocalDateTime.now());
        }
        orderReceiptMapper.insert(request);
        Orders old = orderMapper.selectById(request.getOrderId());
        if (old == null || old.getDeletedTime() != null) {
            throw new BusinessException("该记录不存在");
        }
        //当前欠款小于收款金额 异常
        if (old.getOwe().compareTo(remainingAmount) < 0) {
            throw new BusinessException("当前订单欠款：" + old.getOwe() + "元，小于收款金额" + remainingAmount + "元,请重新提交");
        }
        //修改订单表
        //修改已付金额字段
        old.setPaidAmount(old.getPaidAmount().add(remainingAmount));
        //修改欠款金额字段
        old.setOwe(old.getOwe().subtract(remainingAmount));
        //查询欠款细节
        List<OrderProduct> orderProducts = orderProductMapper.selectList(new LambdaQueryWrapper<OrderProduct>()
                .eq(OrderProduct::getOrderId, request.getOrderId())
        );
        //修改欠款细节
        if (old.getOwe().compareTo(BigDecimal.ZERO) == 0) {
            orderProducts.forEach(x -> {
                x.setOweItem(BigDecimal.ZERO);
            });
        }
        for (OrderProduct x : orderProducts) {
            BigDecimal currentOwe = x.getOweItem();
            BigDecimal newOwe;
            // 欠款金额 > 付款金额
            if (currentOwe.compareTo(remainingAmount) > 0) {
                // 应付款 > 剩余金额：扣减剩余全部，更新oweItem
                newOwe = currentOwe.subtract(remainingAmount);
                remainingAmount = BigDecimal.ZERO; // 剩余金额清零
            } else {
                // 应付款 ≤ 剩余金额：扣减全部应付款，oweItem置0
                newOwe = BigDecimal.ZERO;
                remainingAmount = remainingAmount.subtract(currentOwe); // 剩余金额扣减当前应付款
            }
            x.setOweItem(newOwe);
            orderProductMapper.updateById(x);
            // 剩余金额为0时提前终止
            if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }
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
                try {
                    templateStream.close();
                } catch (IOException ignored) {
                }
            }
            //绝对不要 close(out)
        }
    }

    @Override
    public void exportOwe(HttpServletResponse response, OweDataRequest data) throws IOException {
        log.info(data.toString());
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("欠款对账单");
        // 设置列宽
        double[] realWidths = {9, 8, 9, 9, 7, 15, 13, 10, 4.3};
        for (int i = 0; i < realWidths.length; i++) {
            sheet.setColumnWidth(i, (int) (realWidths[i] * 256));
        }
        // 全局字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);

        //带边框
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setFont(font);
        borderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);

        //居中样式
        CellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setFont(font);
        font.setBold(true);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        //居中带边框
        CellStyle centerAndBorderStyle = workbook.createCellStyle();
        centerAndBorderStyle.setFont(font);
        centerAndBorderStyle.setBorderTop(BorderStyle.THIN);
        centerAndBorderStyle.setBorderBottom(BorderStyle.THIN);
        centerAndBorderStyle.setBorderLeft(BorderStyle.THIN);
        centerAndBorderStyle.setBorderRight(BorderStyle.THIN);
        centerAndBorderStyle.setAlignment(HorizontalAlignment.CENTER);
        centerAndBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 标题字体样式
        Font titleFont = workbook.createFont();
        titleFont.setFontName("宋体");
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 18);
        //标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);

        // 保留两位小数
        CellStyle numberStyle = workbook.createCellStyle();
        numberStyle.setFont(font);
        DataFormat dataFormat = workbook.createDataFormat();
        numberStyle.setDataFormat(dataFormat.getFormat("0.00")); // 0.00表示两位小数
        numberStyle.setAlignment(HorizontalAlignment.CENTER);
        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numberStyle.setBorderTop(BorderStyle.THIN);
        numberStyle.setBorderBottom(BorderStyle.THIN);
        numberStyle.setBorderLeft(BorderStyle.THIN);
        numberStyle.setBorderRight(BorderStyle.THIN);


        int rowNum = 0;

        // 第一行
        Row row = sheet.createRow(rowNum++);
        row.setHeightInPoints(18); // 
        Cell cell = row.createCell(0);
        cell.setCellValue("东莞市广源有机硅科技有限公司");
        cell.setCellStyle(titleStyle);
        forCell(row,titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));


        // 第二行
        row = sheet.createRow(rowNum++);
        forCell(row, borderStyle);
        row.setHeightInPoints(19); // 
        row.createCell(0).setCellValue("TO：");
        row.getCell(0).setCellStyle(borderStyle);
        row.createCell(1).setCellValue(data.getCompanyName());
        row.getCell(1).setCellStyle(borderStyle);
        row.createCell(4).setCellValue("FROM：");
        row.getCell(4).setCellStyle(borderStyle);
        row.createCell(5).setCellValue("东莞市广源有机硅科技有限公司");
        row.getCell(5).setCellStyle(borderStyle);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 5, 8));

        // 第三行
        row = sheet.createRow(rowNum++);
        forCell(row, borderStyle);
        row.setHeightInPoints(22); // 
        row.createCell(0).setCellValue("ATTN：");
        row.getCell(0).setCellStyle(borderStyle);
        row.createCell(1).setCellValue(data.getContactPerson());
        row.getCell(1).setCellStyle(borderStyle);
        row.createCell(4).setCellValue("TEL：");
        row.getCell(4).setCellStyle(borderStyle);
        row.createCell(5).setCellValue("13902603948");
        row.getCell(5).setCellStyle(borderStyle);

        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 8));

        // 第四行
        row = sheet.createRow(rowNum++);
        forCell(row, borderStyle);
        row.setHeightInPoints(20); // 
        row.createCell(0).setCellValue("FAX：");
        row.getCell(0).setCellStyle(borderStyle);
        row.createCell(1).setCellValue(data.getContactPhone());
        row.getCell(1).setCellStyle(borderStyle);
        row.createCell(4).setCellValue("QQ：");
        row.getCell(4).setCellStyle(borderStyle);
        row.createCell(5).setCellValue("799018420");
        row.getCell(5).setCellStyle(borderStyle);

        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 3));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 5, 8));
        // 第五行
        row = sheet.createRow(rowNum++);
        row.createCell(8).setCellStyle(centerAndBorderStyle);
        row.setHeightInPoints(20); // 
        cell = row.createCell(0);
        cell.setCellValue(data.getCutoffDate().getMonthValue() + "月份对账单");
        cell.setCellStyle(centerAndBorderStyle);

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 8));

        // 第六行
        row = sheet.createRow(rowNum++);
        forCell(row, borderStyle);
        row.setHeightInPoints(20); // 
        row.createCell(0).setCellValue("付款条件：" + data.getPaymentTerms());
        row.getCell(0).setCellStyle(borderStyle);
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 2));

        // 表头
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(20); // 
        String[] headers = {
                "日期", "送货单", "规格型号", "单位", "数量", "单价（元）", "金额", "订单号", ""
        };

        for (int i = 0; i < headers.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(centerAndBorderStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 7, 8));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月dd日");
        // 数据行
        for (OweOrderProduct item : data.getOweOrderList()) {

            row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20); // 
            // 为每一列都设置样式
            row.createCell(0).setCellValue(item.getDate().format(formatter));
            row.getCell(0).setCellStyle(centerAndBorderStyle);
            row.createCell(1).setCellValue(item.getDeliveryNoteNumber());
            row.getCell(1).setCellStyle(centerAndBorderStyle);
            row.createCell(2).setCellValue(item.getProductName());
            row.getCell(2).setCellStyle(centerAndBorderStyle);
            row.createCell(3).setCellValue(item.getUnit());
            row.getCell(3).setCellStyle(centerAndBorderStyle);
            row.createCell(4).setCellValue(item.getQuantity());
            row.getCell(4).setCellStyle(centerAndBorderStyle);
            row.createCell(5).setCellValue(item.getUnitPrice().doubleValue());
            row.getCell(5).setCellStyle(numberStyle);
            row.createCell(6).setCellValue(item.getOweItem().doubleValue());
            row.getCell(6).setCellStyle(numberStyle);
            row.createCell(7).setCellValue(item.getOrderNum());
            row.getCell(7).setCellStyle(centerAndBorderStyle);
            row.createCell(8);
            row.getCell(8).setCellStyle(centerAndBorderStyle);

        }

        // 以下空白
        row = sheet.createRow(rowNum++);
        row.createCell(2).setCellValue("以下空白");
        forCell(row, centerAndBorderStyle);

        row = sheet.createRow(rowNum++);
        forCell(row, borderStyle);

        // 小计
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(18); // 
        Cell subtotalCell = row.createCell(0);
        //插入公章
        InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream("templates/gongzhang.png");
        byte[] imageBytes = IOUtils.toByteArray(imageStream);
        imageStream.close();
        //将图片写入Excel的图片容器（XSSFWorkbook专属）
        int pictureIndex = workbook.addPicture(
                imageBytes,
                Workbook.PICTURE_TYPE_PNG // 图片类型：PNG/JPG分别对应PICTURE_TYPE_PNG/JPEG
        );
        // 4. 创建图片绘图对象，定位图片位置
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(
                0,  // 图片左边距（单位：EMU，0为紧贴单元格）
                0,  // 图片上边距
                0,  // 图片右边距
                200000, // 图片下边距
                1, // 图片起始列
                rowNum -3, // 图片起始行
                3, // 图片结束列
                rowNum +3  // 图片结束行
        );

        // 5. 插入图片（公章）
        XSSFPicture picture = drawing.createPicture(anchor, pictureIndex);
        // 可选：调整图片大小（按比例缩放，避免变形）
        picture.resize(1.0); // 1.0为原始大小，0.5为缩小50%，2.0为放大2倍

        subtotalCell.setCellValue("小计");
        subtotalCell.setCellStyle(centerAndBorderStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));
        forCell(row, centerAndBorderStyle);
        row.createCell(6).setCellValue(data.getCurrentTotalAmount().doubleValue());
        row.getCell(6).setCellStyle(numberStyle);

        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("M");

        // 2. 按LocalDate自然顺序（正序）排序，并存入LinkedHashMap
        Map<LocalDate, BigDecimal> sortedMap = data.getMonthlyBreakdown().entrySet().stream()
                // 按key（LocalDate）正序排序（reversed() 可改为倒序）
                .sorted(Map.Entry.comparingByKey())
                // 收集到LinkedHashMap，保留排序顺序
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        // 解决key重复的情况（通常日期不会重复，此处为兜底）
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        for (Map.Entry<LocalDate, BigDecimal> entry : sortedMap.entrySet()) {
            row = sheet.createRow(rowNum++);
            row.setHeightInPoints(18); // 
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 4, 5));
            forCell(row, centerAndBorderStyle);
            row.createCell(4).setCellValue(entry.getKey().format(formatter1) + "月份欠款");
            row.getCell(4).setCellStyle(numberStyle);
            row.createCell(6).setCellValue(entry.getValue().doubleValue());
            row.getCell(6).setCellStyle(numberStyle);
        }

        // 截止金额
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(22); // 
        Cell c = row.createCell(0);
        c.setCellValue("截止" + data.getCutoffDate() + "共欠人民币");
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));
        forCell(row, centerAndBorderStyle);
        row.createCell(6).setCellValue(data.getTotalOweAmount().doubleValue());
        row.getCell(6).setCellStyle(numberStyle);

        //循环合并订单号列
        for (int i = 7; i < rowNum; i++) {
            sheet.addMergedRegion(new CellRangeAddress(i, i, 7, 8));
        }
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        wrapStyle.setFont(font);
        wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
        wrapStyle.setAlignment(HorizontalAlignment.LEFT);
        // remark1
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(36); // 
        row.createCell(0).setCellValue(data.getRemark1());
        row.getCell(0).setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8));


        // remark2
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(36); // 
        row.createCell(0).setCellValue(data.getRemark2());
        row.getCell(0).setCellStyle(wrapStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8));


        // statementDate
        row = sheet.createRow(rowNum++);
        row.createCell(5).setCellValue(data.getStatementDate().toString());
        row.getCell(5).setCellStyle(centerStyle);

        CellStyle bottomBorderStyle = workbook.createCellStyle();
        bottomBorderStyle.setBorderBottom(BorderStyle.THIN);
        // 盖章
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(22); // 
        Cell cellGZ = row.createCell(6);
        cellGZ.setCellValue("盖章：");
        cellGZ.setCellStyle(centerStyle);
        CellStyle underline = workbook.createCellStyle();
        Font underlineFont = workbook.createFont();
        underlineFont.setUnderline(Font.U_SINGLE);
        underline.setFont(underlineFont);

        row.createCell(7).setCellStyle(bottomBorderStyle);
        row.createCell(8).setCellStyle(bottomBorderStyle);

        // 日期
        row = sheet.createRow(rowNum++);
        row.setHeightInPoints(22); // 
        Cell cellRQ = row.createCell(6);
        cellRQ.setCellValue("日期：");
        cellRQ.setCellStyle(centerStyle);
        row.createCell(7).setCellStyle(bottomBorderStyle);
        row.createCell(8).setCellStyle(bottomBorderStyle);

        // 下载
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=owe.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void forCell(Row row, CellStyle cellStyle) {
        for (int i = 0; i < 9; i++) {
            Cell c = row.getCell(i);
            if (c == null) {
                c = row.createCell(i);
            }
            c.setCellStyle(cellStyle);
        }
    }

}