package y9.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import net.risesoft.y9.configuration.feature.openfeign.Y9SignProperties;
import net.risesoft.y9.configuration.feature.openfeign.Y9TokenProperties;
import tools.jackson.databind.json.JsonMapper;
import y9.support.SignInterceptor;
import y9.support.TokenInterceptor;
import y9.support.Y9ErrorDecoder;

/**
 * open feign 自动配置类
 *
 * @author dingzhaojun
 * @date 2022/02/15
 */
@Configuration(proxyBeanMethods = false)
@EnableFeignClients("y9.client")
@EnableConfigurationProperties({ Y9TokenProperties.class, Y9SignProperties.class })
public class Y9FeignConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "errorDecoder")
	public ErrorDecoder errorDecoder(JsonMapper jsonMapper) {
		return new Y9ErrorDecoder(jsonMapper);
	}

	@Bean
	@ConditionalOnProperty(name = "y9.feature.openfeign.token.enabled", havingValue = "true", matchIfMissing = false)
	public RequestInterceptor tokenInterceptor(Y9TokenProperties y9TokenProperties,
			RedisTemplate<String, String> redisTemplate) {
		return new TokenInterceptor(y9TokenProperties, redisTemplate);
	}

	@Bean
	@ConditionalOnProperty(name = "y9.feature.openfeign.sign.enabled", havingValue = "true", matchIfMissing = false)
	public RequestInterceptor signInterceptor(Y9SignProperties y9SignProperties) {
		return new SignInterceptor(y9SignProperties);
	}

}
