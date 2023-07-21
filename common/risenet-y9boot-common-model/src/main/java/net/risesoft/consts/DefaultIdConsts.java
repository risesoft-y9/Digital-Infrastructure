package net.risesoft.consts;

/**
 * 内置实体类的主键常量
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2022/4/21
 */
public class DefaultIdConsts {
    
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
    
    /** 虚拟组织id */
    public static final String ORGANIZATION_VIRTUAL_ID = "11111111-1111-1111-1111-111111111115";
    /** 组织id */
    public static final String ORGANIZATION_TENANT_ID = "11111111-1111-1111-1111-111111111123";
    /** 人员id */
    public static final String PERSON_ID = "11111111-1111-1111-1111-111111111116";
    
    public static final String JOB_ID = "11111111-1111-1111-1111-111111111122";

    /** 系统管理员id */
    public static final String SYSTEM_MANAGER_ID = "11111111-1111-1111-1111-111111111117";
    /** 安全保密员id */
    public static final String SECURITY_MANAGER_ID = "11111111-1111-1111-1111-111111111118";
    /** 安全审计员id */
    public static final String AUDIT_MANAGER_ID = "11111111-1111-1111-1111-111111111119";

    /** 公共角色顶节点id */
    public static final String TOP_PUBLIC_ROLE_ID = "11111111-1111-1111-1111-111111111121";

    private DefaultIdConsts() {
        throw new IllegalStateException("DefaultIdConsts class");
    }

}
