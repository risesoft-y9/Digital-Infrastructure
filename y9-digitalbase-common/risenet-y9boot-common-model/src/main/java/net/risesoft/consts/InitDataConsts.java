package net.risesoft.consts;

/**
 * 初始化数据的常量
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
public class InitDataConsts {

    /** 系统id */
    public static final String SYSTEM_ID = "11111111-1111-1111-1111-111111111111";
    /** 应用id */
    public static final String APP_ID = "11111111-1111-1111-1111-111111111112";

    /** 租户id */
    public static final String TENANT_ID = "11111111-1111-1111-1111-111111111113";
    /** 运维租户id */
    public static final String OPERATION_TENANT_ID = "11111111-1111-1111-1111-111111111120";
    /** 数据源id */
    public static final String DATASOURCE_ID = "11111111-1111-1111-1111-111111111114";

    /** 运维系统管理员id */
    public static final String OPERATION_SYSTEM_MANAGER_ID = "11111111-1111-1111-1111-111111111117";
    /** 运维安全保密员id */
    public static final String OPERATION_SECURITY_MANAGER_ID = "11111111-1111-1111-1111-111111111118";
    /** 运维安全审计员id */
    public static final String OPERATION_AUDIT_MANAGER_ID = "11111111-1111-1111-1111-111111111119";

    public static final String DEFAULT_SYSTEM_MANAGER = "systemManager";
    public static final String DEFAULT_SECURITY_MANAGER = "securityManager";
    public static final String DEFAULT_AUDIT_MANAGER = "auditManager";

    /** 公共角色顶节点id */
    public static final String TOP_PUBLIC_ROLE_ID = "11111111-1111-1111-1111-111111111121";
    /** RC8当前版本(用于数据库存版本号) */
    public static final Integer Y9_VERSION = 0;

    private InitDataConsts() {
        throw new IllegalStateException("InitDataConsts class");
    }

}
