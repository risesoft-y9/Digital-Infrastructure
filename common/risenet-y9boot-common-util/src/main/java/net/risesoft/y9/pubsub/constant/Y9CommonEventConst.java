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
    /** 应用创建，相应系统可监听做一些初始化工作 */
    public static final String APP_CREATED = "APP_CREATED";
    /** 应用配置刷新事件 */
    public static final String REFRESH_REMOTE_APPLICATION_EVENT = "RefreshRemoteApplicationEvent";

}
