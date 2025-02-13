package com.mortal.wms.interceptor;

import com.mortal.wms.annotation.CurrentUser;
import com.mortal.wms.business.entity.Users;
import com.mortal.wms.business.mapper.UsersMapper;
import com.mortal.wms.business.vo.UserVo;
import com.mortal.wms.config.AudienceConfig;
import com.mortal.wms.util.GetIPUtil;
import com.mortal.wms.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Service
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private AudienceConfig audienceConfig;

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.getParameterType().isAssignableFrom(UserVo.class) && parameter.hasParameterAnnotation(CurrentUser.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String token = webRequest.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
        Long userId = JwtTokenUtil.getUserId(token, audienceConfig.getBase64Secret());
        Users users = usersMapper.selectById(userId);
        UserVo userVo = new UserVo();
        userVo.setAdministrator(users.getAdministrator());
        userVo.setUserId(users.getId().longValue());
        userVo.setUserAgent(webRequest.getHeader("User-Agent"));
        userVo.setName(users.getName());
        String ip = GetIPUtil.getIp(getHttpServletRequest());
        userVo.setIp(ip);
        log.info("当前账号编号为>>" + userId);
        return userVo;
    }

    public HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
}
