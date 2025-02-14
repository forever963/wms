package com.mortal.wms.interceptor;

import com.mortal.wms.annotation.JwtIgnore;
import com.mortal.wms.business.enums.ResultTypeEnum;
import com.mortal.wms.business.mapper.UsersMapper;
import com.mortal.wms.config.AudienceConfig;
import com.mortal.wms.execption.BusinessException;
import com.mortal.wms.util.JwtTokenUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ==================
 * token验证拦截器
 * ==================
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private AudienceConfig audienceConfig;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //TODO 优化缓存设计
    //引入Redis分布式缓存 替代本地Map，解决多实例一致性并自动处理过期：
    public static Map<String, LocalDateTime> JwtInterceptorLoginUserMap = new HashMap<>();

    //目标资源方法执行前执行  true放行 false不放行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 忽略静态文件
//        String uri = request.getRequestURL().toString();
//        String[] ignore = new String[]{""};
//        for(String str:ignore){
//            String complete = contextPath+str;
//            if(complete.equals(uri)){
//                return true;
//            }
//        }

        //忽略带JwtIgnore注解的请求 不做后续token验证
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            JwtIgnore jwtIgnore = handlerMethod.getMethodAnnotation(JwtIgnore.class);
            if(jwtIgnore != null) {
                return true;
            }
        }
        //1、从请求头中获取令牌
        final String token = request.getHeader(JwtTokenUtil.AUTH_HEADER_KEY);
        //2、校验令牌
        if(StringUtils.isBlank(token)){
            throw new BusinessException("无法获取登录状态，请重新登录");
        }
        // 验证token是否有效
        JwtTokenUtil.parseJWT(token,audienceConfig.getBase64Secret());
        //输出当前用户信息
        try{
            log.info("currentUserId:{}",JwtTokenUtil.getUserId(token,audienceConfig.getBase64Secret()));
        }catch (Exception e){
            log.info("Get UserId Failed !");
        }
        if(JwtInterceptorLoginUserMap.get(token) == null || LocalDateTime.now().isAfter(JwtInterceptorLoginUserMap.get(token))){
            LocalDateTime expireTime = usersMapper.selectExpireTimeByToken(token);
            if(expireTime==null || LocalDateTime.now().isAfter(expireTime)){
                throw new BusinessException(ResultTypeEnum.PERMISSION_TOKEN_EXPIRED);
            }
        }else{//map中没有过期时间 则设置过期时间为24h
            JwtInterceptorLoginUserMap.put(token,LocalDateTime.now().plusHours(24));
        }

        return true;
    }
}