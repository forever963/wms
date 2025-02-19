package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@TableName("info_categories")
@Data
public class InfoCategories implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String search;
    private String name;
    private Integer type;
    private Integer displayOrder;
    private String description;
    private String data;
    private String module;
}
