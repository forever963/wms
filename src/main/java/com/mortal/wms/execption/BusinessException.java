package com.mortal.wms.execption;

import com.mortal.wms.business.enums.ResultTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException{
    ResultTypeEnum resultTypeEnum;

    public BusinessException(String message){
        this.resultTypeEnum = ResultTypeEnum.SERVICE_FAILED;
        this.resultTypeEnum.setMessage(message);
    }
}