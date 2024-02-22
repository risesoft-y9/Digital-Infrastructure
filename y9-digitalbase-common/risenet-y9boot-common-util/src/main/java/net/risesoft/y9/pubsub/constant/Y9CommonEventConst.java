package net.risesoft.y9.pubsub.constant;

/**
 * 通用事件类型常量
 *
 * @author shidaobang
 * @date 2023/08/22
 * @since 9.6.3
 */
public class Y9CommonEventConst {

    /** 租户数据源同步事件 */
    public static final String TENANT_DATASOURCE_SYNC = "TENANT_DATASOURCE_SYNC";
    /** 租户租用系统事件，相应系统可监听做对应租户的初始化的工作 */
    public static final String TENANT_SYSTEM_REGISTERED = "TENANT_SYSTEM_REGISTERED";
    /** 租户系统已初始化 */
    public static final String TENANT_SYSTEM_INITIALIZED = "TENANT_SYSTEM_INITIALIZED";

}
