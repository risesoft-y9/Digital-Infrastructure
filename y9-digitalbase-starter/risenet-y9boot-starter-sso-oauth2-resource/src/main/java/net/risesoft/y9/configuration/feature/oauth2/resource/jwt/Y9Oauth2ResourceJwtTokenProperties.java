package net.risesoft.y9.configuration.feature.oauth2.resource.jwt;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import lombok.Getter;
import lombok.Setter;

/**
 * oauth2 jwt令牌配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9Oauth2ResourceJwtTokenProperties {

    /**
     * 是否验证 JWT Access Token 签名
     */
    private boolean validationRequired;

    /**
     * JSON Web Key Set 接口路径
     */
    private String jwksUri;

    /**
     * JSON Web Key Set 资源路径
     */
    private Resource jwksLocation = new ClassPathResource("keystore-public.jwks");

}
