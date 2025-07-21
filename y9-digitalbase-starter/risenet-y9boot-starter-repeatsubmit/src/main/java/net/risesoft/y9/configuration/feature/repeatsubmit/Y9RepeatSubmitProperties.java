package net.risesoft.y9.configuration.feature.repeatsubmit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 防重复提交配置属性
 * 
 * @author shidaobang
 * @date 2025/07/18
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.repeat-submit", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9RepeatSubmitProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    private CacheType cacheType = CacheType.LOCAL;

    public enum CacheType {
        LOCAL, REDIS
    }
}
