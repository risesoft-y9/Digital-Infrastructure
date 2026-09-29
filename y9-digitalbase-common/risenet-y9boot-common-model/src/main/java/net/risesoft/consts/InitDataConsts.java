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

    /** 运维租户id */
    public static final String OPERATION_TENANT_ID = "11111111-1111-1111-1111-111111111120";

    public static final String DEFAULT_SYSTEM_MANAGER = "systemManager";
    public static final String DEFAULT_SECURITY_MANAGER = "securityManager";
    public static final String DEFAULT_AUDIT_MANAGER = "auditManager";

    /** 公共角色顶节点id */
    public static final String TOP_PUBLIC_ROLE_ID = "11111111-1111-1111-1111-111111111121";

    private InitDataConsts() {
        throw new IllegalStateException("InitDataConsts class");
    }

}
