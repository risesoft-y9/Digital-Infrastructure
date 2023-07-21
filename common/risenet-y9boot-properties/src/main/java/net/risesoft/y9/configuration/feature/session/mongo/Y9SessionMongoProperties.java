package net.risesoft.y9.configuration.feature.session.mongo;

import lombok.Getter;
import lombok.Setter;

/**
 * mongo 存储 session 配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9SessionMongoProperties {

    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 集合名称
     */
    private String collectionName;
    /**
     * 最大时间间隔
     */
    private Integer maxInactiveIntervalInSeconds;
}
