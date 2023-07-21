package net.risesoft.y9.configuration.feature.session;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.feature.session.mongo.Y9SessionMongoProperties;
import net.risesoft.y9.configuration.feature.session.redis.Y9SessionRedisProperties;

/**
 * session 配置
 *
 * @author liansen
 * @date 2022/09/28
 */
@Getter
@Setter
public class Y9sessionProperties {

    @NestedConfigurationProperty
    private Y9SessionMongoProperties mongo = new Y9SessionMongoProperties();

    @NestedConfigurationProperty
    private Y9SessionRedisProperties redis = new Y9SessionRedisProperties();
    
}
