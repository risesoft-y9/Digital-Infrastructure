package net.risesoft.y9.configuration.app.y9sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9SmsProperties {

    /**
     * 短信开关
     */
    private boolean enable = false;

    private String endpoint;

    private String password;

    private String username;

}
