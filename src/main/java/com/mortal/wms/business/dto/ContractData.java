package com.mortal.wms.business.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ContractData {
    private String contractNumber;
    private String buyerName;
    private String deliveryAddress;
    private String contactPerson;
    private String paymentMethod;
    private String deliveryDate;
    private String contactPhone;
    private List<Product> productList;
    private BigDecimal totalAmount;
    private String totalAmountInWords;
    private String contractExpiryDate;
    private String remark;
    private String nowDate;
}

