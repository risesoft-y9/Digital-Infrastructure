package net.risesoft.y9.configuration.app.y9log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9LogProperties {

    private String accessLogSaveTarget = "console"; // database,console,elastic

    private String accessLogIndex = "logindex";

    private String accessLogType = "log";

    private String indexView = "index";

    private String kafkaEnabled = "false";

    private String kafkaServer;

}
