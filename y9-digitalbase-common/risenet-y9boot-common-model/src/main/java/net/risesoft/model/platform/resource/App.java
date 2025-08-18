package net.risesoft.model.platform.resource;

import lombok.Data;

import net.risesoft.enums.platform.resource.AppOpenTypeEnum;
import net.risesoft.enums.platform.resource.AppTypeEnum;

/**
 * 应用资源
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
@Data
public class App extends Resource {

    private static final long serialVersionUID = 5208672153611659305L;

    /**
     * 系统id
     */
    private String systemId;

    /**
     * 资源别名
     */
    private String aliasName;

    /**
     * 分类 ，1、业务协同，2：事项办理，3：数据服务
     *
     * {@link AppTypeEnum}
     */
    private AppTypeEnum type;

    /**
     * 是否审核通过
     */
    private Boolean checked;

    /**
     * 审核人
     */
    private String verifyUserName;

    /**
     * 是否显示右上角数字，0为不显示，1为显示
     */
    private Boolean showNumber;

    /**
     * 获取数字链接
     */
    private String numberUrl;

    /**
     * 角色管理的URL
     */
    private String roleAdminUrl;

    /**
     * 资源管理的URL
     */
    private String resourceAdminUrl;

    /**
     * 应用打开方式:0在桌面窗口打开；1在新浏览器窗口打开
     *
     * {@link AppOpenTypeEnum}
     */
    private AppOpenTypeEnum opentype;

    /** 图标图片的base64 */
    private String iconData;

    /** 是否自动租用应用 */
    private Boolean autoInit;

}
