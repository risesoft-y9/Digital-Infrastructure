package org.apereo.cas.web.y9.config;

import org.apereo.cas.redis.core.CasRedisTemplate;
import org.apereo.cas.redis.core.RedisObjectFactory;
import org.apereo.cas.web.y9.util.Y9Context;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration(proxyBeanMethods = false)
public class Y9RedisConfig {

    @Bean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    @RefreshScope
    public CasRedisTemplate<Object, Object> y9RedisTemplate(
        @Qualifier("redisTicketConnectionFactory") final RedisConnectionFactory redisTicketConnectionFactory) {
        CasRedisTemplate<Object, Object> redisTemplate =
            RedisObjectFactory.newRedisTemplate(redisTicketConnectionFactory);
        return redisTemplate;
    }
}
