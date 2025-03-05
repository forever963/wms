package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderOutBoundPageRequest extends PageRequest{
    @Schema(name = "客户id")
    private Integer customerInfoId;
}