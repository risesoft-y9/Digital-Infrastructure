package net.risesoft.y9.configuration.app.y9risecloud;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Y9RisecloudProperties {
    /**
     * 应用市场父节点
     */
    private String appParentId;
    /**
     * 解决方案父节点
     */
    private String solutionParentId;
    /**
     * 产品父节点
     */
    private String productParentId;
    /**
     * 客户案例父节点
     */
    private String clientCaseParentId;

    /**
     * 注册默认租户ID
     */
    private String registerTenantId;
    /**
     * 注册默认组织架构ID
     */
    private String registerOrgGuid;

    /**
     * 商业合作
     */
    private String processDefinitionKey1 = "shangyehezuo";
    /**
     * 应聘
     */
    private String processDefinitionKey2 = "yingpin";
    /**
     * 工单管理
     */
    private String processDefinitionKey3 = "gongdanguanli";
    private String temporaryId;
    /**
     * 首页显示的解决方案id
     */
    private String indexSolutionIds;
    /**
     * 点击产品进入的默认的产品id
     */
    private String defaultProductId = "432cbd15b3ce4f409c4202ab850425de";
    /**
     * 点击应用市场进入默认的应用id
     */
    private String defaultAppId = "be7cc6d9f5be41e399506004429115d4";

    /**
     * 管理后台数据的租户id
     */
    private String adminTenantId = "c425281829dc4d4496ddddf7fc0198d0";

    private String emailUsername = "youshengyun@risesoft.net";
    private String emailPassword = "rise1Qa2ws3ed";
    private String emailSmtpHost = "smtp.exmail.qq.com";
    private boolean emailSmtpSsl = true;
    private int emailSmtpPort = 465;

}
