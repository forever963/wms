package com.mortal.wms.business.dto;

import com.mortal.wms.util.PageRequest;
import lombok.Data;

@Data
public class SupplierInfoPageRequest extends PageRequest {
    private String supplierName;
}
