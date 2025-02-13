package com.mortal.wms.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mortal.wms.business.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface UsersMapper extends BaseMapper<Users> {

    @Select("select sso_expire_time from users where sso_token = #{token}")
    LocalDateTime selectExpireTimeByToken(String token);
}
