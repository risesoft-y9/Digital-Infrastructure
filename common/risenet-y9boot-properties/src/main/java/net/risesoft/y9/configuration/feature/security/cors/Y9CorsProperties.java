package net.risesoft.y9.configuration.feature.security.cors;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * cors 跨域配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9CorsProperties {

    // org.springframework.web.cors.CorsConfiguration.class

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 允许的 origin 正则
     */
    private List<String> allowedOriginPatterns = Arrays.asList("https://*.youshengyun.com", "http://*.youshengyun.com",
        "http://localhost:8080", "http://127.0.0.1:8080");

    /**
     * 允许的请求方法
     */
    private List<String> allowedMethods = Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS");

    /**
     * 允许的请求头 目前默认 * 即允许所有请求头
     */
    // private List<String> allowedHeaders = Arrays.asList("x-requested-with", "content-type", "auth-tenantId",
    // "auth-userId", "access_token", "WWW-Authenticate", "token", "y9Site", "Authorization");
    private List<String> allowedHeaders = Arrays.asList("*");

    /**
     * 是否允许发送Cookie
     */
    private boolean allowCredentials = false;

    /**
     * 暴露给客户端的请求头 除了Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma之外的附加响应头
     */
    private List<String> exposedHeaders;

    /**
     * 预检请求有效时长，单位为秒 通过延长预检请求的有效期 可以减少对同一个源的Option请求的数量
     */
    private long maxAge = 86400;
}
