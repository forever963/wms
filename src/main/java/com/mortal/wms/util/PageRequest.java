package com.mortal.wms.util;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(name = "分页查询")
@Data
public class PageRequest implements Serializable {
    @TableField(exist = false)
//    @ExcelIgnore
    @Schema(description = "页码")
    private Integer pageNum;
    @TableField(exist = false)
//    @ExcelIgnore
    @Schema(description = "页长")
    private Integer pageSize;
}
