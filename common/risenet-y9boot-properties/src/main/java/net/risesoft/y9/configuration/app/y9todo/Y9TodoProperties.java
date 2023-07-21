package net.risesoft.y9.configuration.app.y9todo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9TodoProperties {

    private String systemName = "todo";

    private boolean msgpushSwitch = false;

    private String wantHeadMenuGuid;

    private String systemCnName;

    private boolean imSwitch = false;

}
