package net.risesoft.y9.configuration.app.y9addressbook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9AddressBookProperties {

    private String systemName = "risecms7";
    /** 区领导角色 */
    private String allAreaRole;
    /** 两办领导角色 */
    private String unitAreaRole;

    private String defaultContactGroupName;
    private String defaultContactName;

}
