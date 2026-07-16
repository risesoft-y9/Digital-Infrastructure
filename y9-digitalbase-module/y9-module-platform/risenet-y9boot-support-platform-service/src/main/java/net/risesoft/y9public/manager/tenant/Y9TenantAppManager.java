package net.risesoft.y9public.manager.tenant;

import java.util.List;
import java.util.Optional;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;

/**
 * 租户应用 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9TenantAppManager {
    /**
     * 删除租户应用
     *
     * @param y9TenantApp 租户应用信息
     */
    void delete(Y9TenantApp y9TenantApp);

    /**
     * 根据应用id删除租户应用
     *
     * @param appId 应用id
     */
    void deleteByAppId(String appId);

    /**
     * 根据租户id、应用id和租用状态获取租户应用
     *
     * @param tenantId 租户id
     * @param appId 应用id
     * @param tenancy 是否租用
     * @return {@code Optional<Y9TenantApp>}
     */
    Optional<Y9TenantApp> getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);

    /**
     * 根据应用id和租用状态获取租户应用列表
     *
     * @param appId 应用id
     * @param tenancy 是否租用
     * @return {@code List<Y9TenantApp>}
     */
    List<Y9TenantApp> listByAppIdAndTenancy(String appId, Boolean tenancy);

    /**
     * 保存租户应用
     *
     * @param appId 应用id
     * @param tenantId 租户id
     * @param applyReason 申请原因
     * @return {@link Y9TenantApp}
     * @throws Y9NotFoundException appId、tenantId 或应用所属系统对应的记录不存在的情况
     */
    Y9TenantApp save(String appId, String tenantId, String applyReason);

    /**
     * 保存租户应用
     *
     * @param y9TenantApp 租户应用信息
     * @return {@link Y9TenantApp}
     * @throws Y9NotFoundException 租用应用所属的系统不存在的情况
     */
    Y9TenantApp save(Y9TenantApp y9TenantApp);
}
