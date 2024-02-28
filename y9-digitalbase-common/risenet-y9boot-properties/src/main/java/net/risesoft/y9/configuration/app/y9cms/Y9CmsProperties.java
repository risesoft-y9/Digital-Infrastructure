package net.risesoft.y9.configuration.app.y9cms;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9CmsProperties {

    /**
     * 系统名称
     */
    private String systemName = "risecms7";
    /**
     * 人员id
     */
    private String userId;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 罗湖政府在线的文档信息地址
     */
    private String zwdtUrl;
    /**
     * 罗湖政府在线的通知公告地址
     */
    private String tzggUrl;
    /**
     * 罗湖政府在线的社区动态地址
     */
    private String sqdtUrl;

    /**
     * 是否保存至数据中心
     */
    private Boolean dataCenterSwitch = false;

    /**
     * 可访问栏目路径
     */
    private List<String> allowedChannelPath = Collections.singletonList("*");
}
