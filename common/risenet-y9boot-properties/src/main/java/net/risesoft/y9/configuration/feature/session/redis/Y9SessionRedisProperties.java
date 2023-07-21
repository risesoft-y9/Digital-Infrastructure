package net.risesoft.y9.configuration.feature.session.redis;

import lombok.Getter;
import lombok.Setter;

/**
 * redis 存储 session 配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9SessionRedisProperties {
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 名称空间
     */
    private String redisNamespace;
    /**
     * 最大时间间隔
     */
    private Integer maxInactiveIntervalInSeconds = 1800;
    /**
     * 写入模式
     */
    private String flushMode = "ON_SAVE";
}
