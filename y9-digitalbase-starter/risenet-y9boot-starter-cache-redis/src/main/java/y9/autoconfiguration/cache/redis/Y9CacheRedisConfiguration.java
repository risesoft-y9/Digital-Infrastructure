package y9.autoconfiguration.cache.redis;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.KotlinDetector;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

@Configuration
@EnableConfigurationProperties(CacheProperties.class)
@EnableCaching(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@ConditionalOnProperty(name = "y9.common.cacheEnabled", havingValue = "true", matchIfMissing = true)
public class Y9CacheRedisConfiguration implements CachingConfigurer {

    @Autowired
    private CacheProperties cacheProperties;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManagerBuilder builder =
            RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(determineConfiguration());
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        return builder.build();
    }

    private RedisCacheConfiguration determineConfiguration() {
        Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        StdTypeResolverBuilder typer = new TypeResolverBuilder(ObjectMapper.DefaultTyping.EVERYTHING,
                objectMapper.getPolymorphicTypeValidator());
        typer = typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = typer.inclusion(JsonTypeInfo.As.PROPERTY);
        objectMapper.setDefaultTyping(typer);

        config = config.serializeValuesWith(
            SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));

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

        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName() + ":");
            sb.append(method.getName() + ":");
            for (Object param : params) {
                if (param != null) {
                    sb.append(param.toString() + ":");
                }
            }
            return sb.toString().substring(0, sb.length() - 1);
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    private static class TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        public TypeResolverBuilder(ObjectMapper.DefaultTyping t, PolymorphicTypeValidator ptv) {
            super(t, ptv);
        }

        @Override
        public ObjectMapper.DefaultTypeResolverBuilder withDefaultImpl(Class<?> defaultImpl) {
            return this;
        }

        /**
         * Method called to check if the default type handler should be used for given type. Note: "natural types" (String,
         * Boolean, Integer, Double) will never use typing; that is both due to them being concrete and final, and since
         * actual serializers and deserializers will also ignore any attempts to enforce typing.
         */
        public boolean useForType(JavaType t) {

            if (t.isJavaLangObject()) {
                return true;
            }

            t = resolveArrayOrWrapper(t);

            if (t.isEnumType() || ClassUtils.isPrimitiveOrWrapper(t.getRawClass())) {
                return false;
            }

            if (t.isFinal() && !KotlinDetector.isKotlinType(t.getRawClass())
                    && t.getRawClass().getPackage().getName().startsWith("java")) {
                return false;
            }

            // [databind#88] Should not apply to JSON tree models:
            return !TreeNode.class.isAssignableFrom(t.getRawClass());
        }

        private JavaType resolveArrayOrWrapper(JavaType type) {

            while (type.isArrayType()) {
                type = type.getContentType();
                if (type.isReferenceType()) {
                    type = resolveArrayOrWrapper(type);
                }
            }

            while (type.isReferenceType()) {
                type = type.getReferencedType();
                if (type.isArrayType()) {
                    type = resolveArrayOrWrapper(type);
                }
            }

            return type;
        }
    }
}
