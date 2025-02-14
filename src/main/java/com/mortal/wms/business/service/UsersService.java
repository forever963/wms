package com.mortal.wms.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mortal.wms.business.dto.UsersLoginRequest;
import com.mortal.wms.business.entity.Users;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface UsersService extends IService<Users> {

    ResultResponse login(UsersLoginRequest userLoginQo, HttpServletRequest request, HttpServletResponse response);

    ResultResponse addUser(UserVo userVo, Users users);

    ResultResponse detail(UserVo userVo,Integer id);

    ResultResponse list(PageRequest request);

    ResultResponse delete(UserVo userVo,Integer id);

    ResultResponse update(UserVo userVo, @Valid Users users);
}
