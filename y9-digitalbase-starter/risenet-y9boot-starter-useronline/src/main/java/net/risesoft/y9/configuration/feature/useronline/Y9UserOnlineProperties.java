package net.risesoft.y9.configuration.feature.useronline;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户在线上报属性
 *
 * @author shidaobang
 * @date 2025/03/06
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "y9.feature.useronline", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9UserOnlineProperties {

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 在线消息上报方法
     */
    private ReportMethod reportMethod = ReportMethod.API;

    @Getter
    @AllArgsConstructor
    public enum ReportMethod {
        API, KAFKA
    }
}
