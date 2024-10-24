package net.risesoft.y9.configuration.app.y9log;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "y9.app.log", ignoreInvalidFields = true, ignoreUnknownFields = true)
public class Y9LogProperties {

    private String accessLogSaveTarget = "console"; // database,console,elastic

    private String accessLogIndex = "logindex";

    private String accessLogType = "log";

    private String kafkaEnabled = "false";

}
