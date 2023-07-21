package net.risesoft.y9.configuration.app.y9home;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9HomeProperties {

    private String articleUrl = "http://www.youshengyun.com/subscription/user/article?articleid=";

    private String dataUrl = "http://www.youshengyun.com/dataservice/dydata/market";

    private String instantDataBaseUrl = "http://www.youshengyun.com/dataservice/financialIndex";

    private String itemAdminUrl = "http://www.youshengyun.com/itemAdmin";

    private String processAdminUrl = "http://www.youshengyun.com/processAdmin/main/index";

    private String subscriptionBaseUrl = "http://www.youshengyun.com/subscription";

    private String processAndItemRole = "流程事项角色";

    private String systemCnName = "内网门户";

    private String systemName = "y9home";

    private String workUrl = "http://www.youshengyun.com/flowableUI/newIndex";

    private String recommendUrl = "http://www.youshengyun.com/subscription";

    private String popupRoleName = "系统弹窗类管理员";

}
