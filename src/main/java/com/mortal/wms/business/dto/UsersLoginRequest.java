package com.mortal.wms.business.dto;

import com.mortal.wms.util.Base64Util;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户登录")
public class UsersLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "手机号")
    private String phoneNumber;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    public String getPassword() {
        String decode = Base64Util.decode(password);
        return decode;
    }
}