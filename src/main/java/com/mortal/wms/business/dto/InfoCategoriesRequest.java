package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import lombok.Data;

@Data
public class InfoCategoriesRequest extends PageRequest {
    Integer type;
}
