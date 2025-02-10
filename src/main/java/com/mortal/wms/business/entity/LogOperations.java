package com.mortal.wms.business.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("log_operations")
@Schema(name = "操作日志")
@Data
public class LogOperations implements Serializable {
    @TableId(type = IdType.AUTO)
    @Schema(name = "ID")
    private Integer id;
    @NotNull(message = "用户id不能为空")
    @Schema(name = "用户id",required = true)
    private Long userId;
    //不需要持久化到数据库的字段
    @TableField(exist = false)
    private String userName;
    @Schema(name = "用户代理")
    private String userAgent;
    @Schema(name = "ip")
    private String ip;
    @Schema(name = "内容")
    private String content;
    @JSONField(format = "yyyy-MM-dd HH:mm")
    @Schema(name = "创建时间", hidden = true)
    private LocalDateTime createdTime;
}
