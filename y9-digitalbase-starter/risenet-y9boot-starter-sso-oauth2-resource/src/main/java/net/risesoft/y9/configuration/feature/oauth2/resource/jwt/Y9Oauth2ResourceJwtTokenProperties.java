package net.risesoft.y9.configuration.feature.oauth2.resource.jwt;

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
     * jwk uri
     */
    private String jwkSetUri;

    /**
     * jw算法
     */
    private String jwsAlgorithm = "RS256";

    /**
     * uri
     */
    private String issuerUri;

    /**
     * 公共密钥位置
     */
    private Resource publicKeyLocation;

}
