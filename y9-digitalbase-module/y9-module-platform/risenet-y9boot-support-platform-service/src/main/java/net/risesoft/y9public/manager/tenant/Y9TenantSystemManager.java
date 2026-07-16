package net.risesoft.y9public.manager.tenant;

import java.util.List;

import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;

/**
 * 租户系统 Manager
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
public interface Y9TenantSystemManager {
    /**
     * 根据id删除租户系统
     *
     * @param id 租户系统id
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    void delete(String id);

    /**
     * 根据id获取租户系统信息
     *
     * @param id 租户系统id
     * @return {@link Y9TenantSystem}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9TenantSystem getById(String id);

    /**
     * 根据系统id删除租户系统
     *
     * @param systemId 系统id
     */
    void deleteBySystemId(String systemId);

    /**
     * 根据租户id和系统id获取数据源id
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     * @return 数据源id
     */
    String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 保存租户系统
     *
     * @param y9TenantSystem 租户系统信息
     * @return {@link Y9TenantSystem}
     * @throws Y9NotFoundException 系统id对应的记录不存在的情况
     */
    Y9TenantSystem save(Y9TenantSystem y9TenantSystem);

    /**
     * 保存指定租户的系统
     *
     * @param systemId 系统id
     * @param tenantId 租户id
     * @return {@link Y9TenantSystem}
     * @throws Y9NotFoundException systemId 或 tenantId 对应的记录不存在的情况
     */
    Y9TenantSystem saveTenantSystem(String systemId, String tenantId);

    /**
     * 批量保存指定租户的系统
     *
     * @param systemIds 系统id列表
     * @param tenantId 租户id
     * @return {@code List<Y9TenantSystem>}
     * @throws Y9NotFoundException systemIds 中的系统id或 tenantId 对应的记录不存在的情况
     */
    List<Y9TenantSystem> saveTenantSystems(List<String> systemIds, String tenantId);
}
