package com.mortal.wms.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.annotation.JwtIgnore;
import com.mortal.wms.business.dto.UsersLoginRequest;
import com.mortal.wms.business.entity.Users;
import com.mortal.wms.business.service.UsersService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.PageRequest;
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
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@Slf4j
@Tag(name = "用户",description = "用户接口")
public class UsersController {
    @Autowired
    private UsersService userService;

    @JwtIgnore
    @Operation(summary = "登录")
    @PostMapping("/login")
    public ResultResponse login(@Valid @RequestBody UsersLoginRequest userLoginQo, HttpServletRequest request, HttpServletResponse response) {
        ResultResponse login = userService.login(userLoginQo, request, response);
        return login;
    }

    @JwtIgnore
    @Operation(summary = "新增")
    @PostMapping("/user")
    public ResultResponse addUser(@Valid @RequestBody Users users) throws ParseException {
        ResultResponse response = userService.addUser(users);
        return response;
    }

    @Operation(summary = "详情")
    @GetMapping("/detail/{id}")
    public ResultResponse detail(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse response = userService.detail(userVo,id);
        return response;
    }

    @Operation(summary = "列表")
    @GetMapping("/list")
    public ResultResponse list(PageRequest request) {
        ResultResponse response = userService.list(request);
        return response;
    }

    @Operation(summary = "删除")
    @PutMapping("/delete/{id}")
    public ResultResponse delete(@CurrentUser UserVo userVo,@PathVariable Integer id) {
        ResultResponse response = userService.delete(userVo,id);
        return response;
    }

    @Operation(summary = "编辑")
    @PutMapping("/update")
    public ResultResponse update(@CurrentUser UserVo userVo,@Valid @RequestBody Users users) {
        ResultResponse response = userService.update(userVo,users);
        return response;
    }
}