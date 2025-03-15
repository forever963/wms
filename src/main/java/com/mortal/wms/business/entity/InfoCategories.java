package com.mortal.wms.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@TableName("info_categories")
@Data
public class InfoCategories implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @Schema(description = "简称")
    private String search;
    @Schema(description = "字典名")
    private String name;
    @Schema(description = "字典类型")
    private Integer type;
    @Schema(description = "字典排序")
    private Integer displayOrder;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "数据")
    private String data;
    @Schema(description = "隶属模块")
    private String module;
}
