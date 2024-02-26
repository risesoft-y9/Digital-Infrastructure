package net.risesoft.y9.configuration.app.y9scalendar.leadercalendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9LeaderCalendarProperties {

    /**
     * 领导日程菜单id
     */
    private String resourceId;

    /**
     * 区领导日程仅展示给区委区政府的成员看
     */
    private Boolean showToCentralOrgUnitOnly = true;

    /**
     * 区委区政府id 当 showToCentralOrgUnitOnly = true 时需设置
     */
    private String centralOrgUnitId = "all";

}
