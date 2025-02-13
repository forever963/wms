package com.mortal.wms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "audience")
@Component
public class AudienceConfig {
    private String clientId;
    private String base64Secret;
    private String name;
    //密钥过期时间
    private long expiresSecond;
}
