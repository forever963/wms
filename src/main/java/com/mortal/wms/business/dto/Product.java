package com.mortal.wms.business.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Product {
    private String name;
    private String model;
    private String unit;
    private Integer qty;
    private BigDecimal price;
    private BigDecimal amount;
    private String remark;
    // getters & setters
}