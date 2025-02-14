package com.mortal.wms.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mortal.wms.business.dto.UsersLoginRequest;
import com.mortal.wms.business.entity.LogOperations;
import com.mortal.wms.business.entity.Users;
import com.mortal.wms.business.mapper.UsersMapper;
import com.mortal.wms.business.service.LogOperationsService;
import com.mortal.wms.business.service.UsersService;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.config.AudienceConfig;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.Argon2Util;
import com.mortal.wms.util.JwtTokenUtil;
import com.mortal.wms.util.PageRequest;
import com.mortal.wms.util.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private LogOperationsService logOperationsService;
    @Autowired
    private AudienceConfig audienceConfig;


    @Override
    public ResultResponse login(UsersLoginRequest userLoginQo, HttpServletRequest request, HttpServletResponse response) {
        try {
            //检查用户是否存在
            Users users = usersMapper.selectOne(new LambdaUpdateWrapper<Users>()
                    .eq(Users::getPhone, userLoginQo.getPhoneNumber())
            );
            if (users == null) {
                return ResultResponse.error("账号不存在");
            }
            // 验证密码
            if (!Argon2Util.isValidPassword(userLoginQo.getPassword(), users.getPassword())) {
                return ResultResponse.error("密码错误");
            }
            //设置token过期时间
            audienceConfig.setExpiresSecond(1000L * 60 * 60 * 24 * 365 * 100);
            //生成 JWT 并更新用户会话
            String jwt = JwtTokenUtil.createJWT(users.getId(), audienceConfig);
            users.setSsoToken(jwt);
            users.setSsoExpireTime(LocalDateTime.now().plusSeconds(60 * 60 * 24));
            usersMapper.updateById(users);
            response.setHeader(JwtTokenUtil.AUTH_HEADER_KEY, jwt);
            //返回用户信息
            return ResultResponse.success(users);

        } catch (Exception e) {
            throw new RuntimeException("登录处理失败", e);
        }
    }

    @Override
    @Transactional
    public ResultResponse addUser(UserVo userVo, Users users) {
        if (usersMapper.selectCount(new LambdaUpdateWrapper<Users>()
                .eq(Users::getPhone, users.getPassword())
                .isNull(Users::getDeletedTime)
        ) != 0) {
            throw new BusinessException("手机号已存在");
        }
        ;
        if (usersMapper.selectCount(new LambdaUpdateWrapper<Users>()
                .eq(Users::getIdCard, users.getIdCard())
                .isNull(Users::getDeletedTime)
        ) != 0) {
            throw new BusinessException("身份证号已存在");
        }
        users.setCreatedTime(LocalDateTime.now());
        users.setPassword(Argon2Util.hashPassword(users.getPassword()));
        usersMapper.insert(users);

        LogOperations logOperationsEntity = new LogOperations();
        logOperationsEntity.setContent("添加了用户:" + users.getName());
        logOperationsEntity.setCreatedTime(LocalDateTime.now());
        logOperationsService.addLogOperations(userVo, logOperationsEntity);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse detail(UserVo userVo, Integer id) {
        Users old = usersMapper.selectOne(new LambdaUpdateWrapper<Users>().eq(Users::getId, id).isNull(Users::getDeletedTime));
        if (old == null) {
            throw new BusinessException("该记录不存在");
        }
        return ResultResponse.success(old);
    }

    @Override
    public ResultResponse list(PageRequest request) {
        Page<Users> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Users> page1 = usersMapper.selectPage(page, new LambdaQueryWrapper<Users>()
                        .select(Users::getId,Users::getIdCard,Users::getName,Users::getGender,Users::getNation
                        ,Users::getPosition,Users::getNativePlace,Users::getPhone,Users::getContractStartDate,
                                Users::getContractEndDate,Users::getAdministrator,Users::getCreatedTime
                        )
                .isNull(Users::getDeletedTime)
        );
        return ResultResponse.success(page1);
    }

    @Override
    public ResultResponse delete(UserVo userVo, Integer id) {
        Users old = usersMapper.selectOne(new LambdaUpdateWrapper<Users>()
                .eq(Users::getId, id)
                .isNull(Users::getDeletedTime)
        );
        if (old == null) {
            throw new BusinessException("该记录不存在");
        }
        old.setDeletedTime(LocalDateTime.now());
        usersMapper.updateById(old);
        return ResultResponse.success();
    }

    @Override
    public ResultResponse update(UserVo userVo, Users users) {
        Users old = usersMapper.selectOne(new LambdaUpdateWrapper<Users>()
                .eq(Users::getId, users.getId())
                .isNull(Users::getDeletedTime)
        );
        old.setModifiedTime(LocalDateTime.now());
        old.setIdCard(users.getIdCard());
        old.setName(users.getName());
        old.setGender(users.getGender());
        old.setNation(users.getNation());
        old.setPosition(users.getPosition());
        old.setNativePlace(users.getNativePlace());
        old.setContractStartDate(users.getContractStartDate());
        old.setContractEndDate(users.getContractEndDate());
        usersMapper.updateById(old);
        return ResultResponse.success();
    }

}