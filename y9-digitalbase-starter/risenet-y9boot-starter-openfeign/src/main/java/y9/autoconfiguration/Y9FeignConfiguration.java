package y9.autoconfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.y9.configuration.Y9Properties;

import y9.support.TokenInterceptor;
import y9.support.Y9ErrorDecoder;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;

/**
 * open feign 自动配置类
 *
 * @author dingzhaojun
 * @date 2022/02/15
 */
@Configuration(proxyBeanMethods = false)
@EnableFeignClients("y9.client")
public class Y9FeignConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "errorDecoder")
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new Y9ErrorDecoder(objectMapper);
    }
    
    @Bean
    @ConditionalOnProperty(name = "y9.feature.api.token-required", havingValue = "true", matchIfMissing = false)
    public RequestInterceptor tokenInterceptor(Y9Properties y9Properties, RedisTemplate<String, String> redisTemplate) {
        return new TokenInterceptor(y9Properties, redisTemplate);
    } 

}
