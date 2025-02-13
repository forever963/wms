package com.mortal.wms.util;

import com.mortal.wms.business.enums.ResultTypeEnum;
import com.mortal.wms.config.AudienceConfig;
import com.mortal.wms.execption.BusinessException;
import io.jsonwebtoken.*;
import jakarta.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {

    private static Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);

    public static final String AUTH_HEADER_KEY = "Authorization";

    public static final String TOKEN_MORE_PREFIX = "ProfilesBearer ";

    // 定义一些私有的可以放在Token中的信息
    public static final String USER_ID = "UserId";
//    public static final String INSIDE = "Inside";
    public static final String ID = "Id";
    //organization_id
    public static final String ORGANIZATION_ID = "organizationId";

    /**
     * 解析jwt
     * @param jsonWebToken
     * @param base64Security
     * @return
     */
    public static Claims parseJWT(String jsonWebToken, String base64Security) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Security))
                    .parseClaimsJws(jsonWebToken).getBody();
            return claims;
        } catch (ExpiredJwtException eje) {
            log.error("===== Token过期 =====", eje);
            throw new BusinessException(ResultTypeEnum.PERMISSION_TOKEN_EXPIRED);
        } catch (Exception e){
            log.error("===== token解析异常 =====", e);
            throw new BusinessException(ResultTypeEnum.PERMISSION_TOKEN_INVALID);
        }
    }

    /**
     * 构建jwt
     * @param userId
     * @param audienceConfig
     * @return
     */
    public static String createJWT(Long userId, AudienceConfig audienceConfig) {
        try {
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);

            //生成签名密钥
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(audienceConfig.getBase64Secret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            //添加构成JWT的参数
            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                    // 可以将基本不重要的对象信息放到claims
                    .claim(USER_ID, userId)
                    // 代表这个JWT的主体，即它的所有人
                    //.setSubject(username)
                    // 代表这个JWT的签发主体；
                    .setIssuer(audienceConfig.getClientId())
                    // 是一个时间戳，代表这个JWT的签发时间；
                    .setIssuedAt(new Date())
                    // 代表这个JWT的接收对象；
                    .setAudience(audienceConfig.getName())
                    .signWith(signatureAlgorithm, signingKey);
            //添加Token过期时间
            long TTLMillis = audienceConfig.getExpiresSecond();
            if (TTLMillis >= 0) {
                long expMillis = nowMillis + TTLMillis;
                Date exp = new Date(expMillis);
                // 是一个时间戳，代表这个JWT的过期时间；
                builder.setExpiration(exp)
                        // 是一个时间戳，代表这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的
                        .setNotBefore(now);
            }

            // 生成JWT
            return builder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new BusinessException(ResultTypeEnum.PERMISSION_SIGNATURE_ERROR);
        }
    }

//    public static String createJWTMore(Long id, AudienceConfig audienceConfig) {
//        try {
//            // 使用HS256加密算法
//            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//            long nowMillis = System.currentTimeMillis();
//            Date now = new Date(nowMillis);
//
//            //生成签名密钥
//            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(audienceConfig.getBase64Secret());
//            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
//
//            //添加构成JWT的参数
//            JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
//                    // 可以将基本不重要的对象信息放到claims
//                    .claim(ID, id)
//                    // 代表这个JWT的主体，即它的所有人
//                    //.setSubject(username)
//                    // 代表这个JWT的签发主体；
//                    .setIssuer(audienceConfig.getClientId())
//                    // 是一个时间戳，代表这个JWT的签发时间；
//                    .setIssuedAt(new Date())
//                    // 代表这个JWT的接收对象；
//                    .setAudience(audienceConfig.getName())
//                    .signWith(signatureAlgorithm, signingKey);
//            //添加Token过期时间
//            long TTLMillis = audienceConfig.getExpiresSecond();
//            if (TTLMillis >= 0) {
//                long expMillis = nowMillis + TTLMillis;
//                Date exp = new Date(expMillis);
//                // 是一个时间戳，代表这个JWT的过期时间；
//                builder.setExpiration(exp)
//                        // 是一个时间戳，代表这个JWT生效的开始时间，意味着在这个时间之前验证JWT是会失败的
//                        .setNotBefore(now);
//            }
//
//            // 生成JWT
//            return builder.compact();
//        } catch (Exception e) {
//            log.error("签名失败", e);
//            throw new BusinessException(ResultTypeEnum.PERMISSION_SIGNATURE_ERROR);
//        }
//    }

 /**
     * 从token中获取用户ID
     * @param token
     * @param base64Security
     * @return
     */
    public static Long getUserId(String token, String base64Security){
        Long userId = parseJWT(token, base64Security).get(USER_ID, Long.class);
        return userId;
    }

    public static Long getId(String token, String base64Security){
        Long userId = parseJWT(token, base64Security).get(ID, Long.class);
        return userId;
    }


    /**
     * 是否已过期
     * @param token
     * @param base64Security
     * @return
     */
    public static boolean isExpiration(String token, String base64Security) {
        return parseJWT(token, base64Security).getExpiration().before(new Date());
    }
}
