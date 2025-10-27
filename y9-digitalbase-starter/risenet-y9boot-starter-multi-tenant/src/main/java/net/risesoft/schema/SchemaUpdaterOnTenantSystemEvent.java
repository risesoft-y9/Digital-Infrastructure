package net.risesoft.schema;

/**
 * 租户数据结构更新
 *
 * @author shidaobang
 * @date 2023/11/20
 * @since 9.6.3
 */
public interface SchemaUpdaterOnTenantSystemEvent {

    void doUpdate(String tenantId);

}
