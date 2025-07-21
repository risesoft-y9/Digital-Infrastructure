package y9.autoconfiguration.repeatsubmit;

import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import net.risesoft.permission.aop.RepeatSubmitAdvice;
import net.risesoft.permission.aop.RepeatSubmitAdvisor;
import net.risesoft.permission.cache.LocalSubmitCache;
import net.risesoft.permission.cache.RedisSubmitCache;
import net.risesoft.permission.cache.SubmitCache;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.feature.repeatsubmit.Y9RepeatSubmitProperties;

/**
 * 防重复提交配置类
 *
 * @author shidaobang
 * @date 2025/07/21
 */
@Configuration
@EnableConfigurationProperties(Y9RepeatSubmitProperties.class)
@ConditionalOnProperty(prefix = "y9.feature.repeat-submit", name = "enabled", havingValue = "true",
    matchIfMissing = true)
public class Y9RepeatSubmitConfiguration {

    @Bean
    @ConditionalOnMissingBean(AbstractAdvisorAutoProxyCreator.class)
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator bean = new DefaultAdvisorAutoProxyCreator();
        bean.setProxyTargetClass(true);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(RepeatSubmitAdvice.class)
    public RepeatSubmitAdvice repeatSubmitAdvice(SubmitCache submitCache) {
        return new RepeatSubmitAdvice(submitCache);
    }

    @Bean
    @ConditionalOnMissingBean(RepeatSubmitAdvisor.class)
    public RepeatSubmitAdvisor repeatSubmitAdvisor(RepeatSubmitAdvice repeatSubmitAdvice) {
        RepeatSubmitAdvisor bean = new RepeatSubmitAdvisor();
        bean.setAdvice(repeatSubmitAdvice);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public Y9Context y9Context() {
        return new Y9Context();
    }

    @Bean
    public SubmitCache defaultKeyCache() {
        return new LocalSubmitCache();
    }

    @Bean
    @Primary
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(prefix = "y9.feature.repeat-submit", name = "cache-type", havingValue = "REDIS")
    public SubmitCache redisKeyCache(StringRedisTemplate redisTemplate) {
        return new RedisSubmitCache(redisTemplate);
    }
}
