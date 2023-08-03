package y9.autoconfiguration.cache.redis;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@ConditionalOnProperty(name = "y9.common.cacheEnabled", havingValue = "true", matchIfMissing = true)
public class Y9CacheRedisConfiguration extends CachingConfigurerSupport {

    @Autowired
    private CacheProperties cacheProperties;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManagerBuilder builder =
            RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(determineConfiguration(resourceLoader));
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        return builder.build();
    }

    private RedisCacheConfiguration determineConfiguration(ResourceLoader resourceLoader) {
        Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = config.serializeValuesWith(
            SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(resourceLoader.getClassLoader())));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        // return new SimpleKeyGenerator();

        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName() + ":");
                sb.append(method.getName() + ":");
                for (Object param : params) {
                    if (param != null) {
                        sb.append(param.toString() + ":");
                    }
                }
                return sb.toString().substring(0, sb.length() - 1);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}
