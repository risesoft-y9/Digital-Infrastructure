package net.risesoft.y9.configuration.app.y9log;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.log", ignoreInvalidFields = true)
public class Y9LogProperties {

    private String accessLogType = "log";

    private String kafkaEnabled = "false";

}
