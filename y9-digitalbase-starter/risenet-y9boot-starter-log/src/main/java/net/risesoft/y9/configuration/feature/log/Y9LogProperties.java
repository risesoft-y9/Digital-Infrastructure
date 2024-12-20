package net.risesoft.y9.configuration.feature.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 日志配置
 *
 * @author shidaobang
 * @date 2022/09/30
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.log", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9LogProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /** 日志信息上报方式 */
    private LogSaveTarget logSaveTarget = LogSaveTarget.KAFKA;

    @Getter
    @AllArgsConstructor
    public enum LogSaveTarget {
        API, KAFKA
    }

}
