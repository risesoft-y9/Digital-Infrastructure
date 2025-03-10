package net.risesoft.y9.configuration.feature.oauth2.resource.jwt;

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
	 * JSON Web Key Set 资源路径
	 * 支持格式：classpath:*;https://*;file://*
	 */
	private String jwksLocation = "classpath:keystore-public.jwks";

}
