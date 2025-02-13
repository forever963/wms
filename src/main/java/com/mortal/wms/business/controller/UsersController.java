package com.mortal.wms.business.controller;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.annotation.JwtIgnore;
import com.mortal.wms.business.dto.UsersLoginRequest;
import com.mortal.wms.business.entity.Users;
import com.mortal.wms.business.service.UsersService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@Slf4j
@Tag(name = "用户",description = "用户接口")
public class UsersController {
    @Autowired
    private UsersService userService;

    @JwtIgnore
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResultResponse login(@Valid @RequestBody UsersLoginRequest userLoginQo, HttpServletRequest request, HttpServletResponse response) {
        ResultResponse login = userService.login(userLoginQo, request, response);
        return login;
    }

    @Operation(summary = "用户新增")
    @PostMapping("/user")
    public ResultResponse addUser(@CurrentUser UserVo userVo, @Valid @RequestBody Users users) throws ParseException {
        ResultResponse response = userService.addUser(userVo, users);
        return response;
    }
}