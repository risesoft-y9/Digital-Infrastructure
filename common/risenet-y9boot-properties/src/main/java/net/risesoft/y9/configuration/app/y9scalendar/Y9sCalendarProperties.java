package net.risesoft.y9.configuration.app.y9scalendar;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.y9.configuration.app.y9scalendar.leadercalendar.Y9LeaderCalendarProperties;

@Getter
@Setter
public class Y9sCalendarProperties {

    @NestedConfigurationProperty
    private Y9LeaderCalendarProperties leaderCalendar = new Y9LeaderCalendarProperties();

    private boolean pushToWeChatEnabled = false;

    private boolean pushToAppEnabled = false;

    private String weChatApiBaseUrl;

    /**
     * 父菜单资源id
     */
    private String menuId;

    /**
     * 开启日程发送短信（需有短信平台）
     */
    private boolean smsEnabled = false;

}
