package com.mortal.wms.business.util;

import com.mortal.wms.business.enums.ResultTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Tag(name = "接口通用返回对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ResultResponse<T> implements Serializable {

    @Schema(required = true, description = "结果码", example = "0")
    private Integer code;
    @Schema(required = true, description = "说明信息", example = "成功")
    private String msg;
    @Schema(required = true, description = "返回数据")
    private T data;

    public ResultResponse(ResultTypeEnum type) {
        this.code = type.getCode();
        this.msg = type.getMessage();
    }

    public ResultResponse(ResultTypeEnum type, T data) {
        this.code = type.getCode();
        this.msg = type.getMessage();
        this.data = data;
    }

    public ResultResponse(ResultTypeEnum type, String content, T data) {
        this.code = type.getCode();
        this.msg = content;
        this.data = data;
    }

    public static ResultResponse success() {
        ResultResponse resultResponse = new ResultResponse(ResultTypeEnum.SERVICE_SUCCESS);
        return resultResponse;
    }

    public static <T> ResultResponse<T> success(T data) {
        ResultResponse resultResponse = new ResultResponse(ResultTypeEnum.SERVICE_SUCCESS, data);
        return resultResponse;
    }

    public static <T> ResultResponse<T> error(T data) {
        return new ResultResponse(ResultTypeEnum.SERVICE_FAILED, data);
    }

    public static <T> ResultResponse<T> success(String content, T data) {
        return new ResultResponse(ResultTypeEnum.SERVICE_SUCCESS, content, data);
    }

    public static ResultResponse error() {
        return new ResultResponse(ResultTypeEnum.SERVICE_ERROR);
    }

    public static ResultResponse error(ResultTypeEnum typeEnum) {
        return new ResultResponse(typeEnum);
    }

    public static ResultResponse error(ResultTypeEnum typeEnum, String msg) {
        return new ResultResponse(typeEnum, msg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}