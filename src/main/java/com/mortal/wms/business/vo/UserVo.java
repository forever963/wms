package com.mortal.wms.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "userVo")
public class UserVo {
    private Integer id;
    private Long userId;
    private String name;
    private String userAgent;
    private String ip;
    private Boolean administrator;
}
