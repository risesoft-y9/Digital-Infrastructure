package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.System;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.model.platform.tenant.TenantSystem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9TenantSystemService {

    /**
     * 根据id删除租户应用
     *
     * @param id 租户应用主键id
     */
    void delete(String id);

    /**
     * 按租户id删除
     *
     * @param tenantId 租户id
     */
    void deleteByTenantId(String tenantId);

    /**
     * 根据租户id和系统id删除
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     */
    void deleteByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据id查找租户应用
     *
     * @param id 租户应用主键id
     * @return {@code Optional<}{@link TenantSystem}{@code >}
     */
    Optional<TenantSystem> findById(String id);

    /**
     * 通过租户id和系统id
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     * @return {@code Optional<}{@link TenantSystem}{@code >}
     */
    Optional<TenantSystem> getByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据系统唯一标示查找租户系统集合
     *
     * @param systemId 系统id
     * @return {@code List<}{@link TenantSystem}{@code >}
     */
    List<TenantSystem> listBySystemId(String systemId);

    List<System> listSystemByTenantId(String tenantId);

    /**
     * 根据租户id查询所关联的系统id列表
     *
     * @param tenantId 租户id
     * @return {@code List<}{@link String}{@code >}
     */
    List<String> listSystemIdByTenantId(String tenantId);

    List<Tenant> listTenantBySystemId(String systemId);

    List<Tenant> listTenantBySystemName(String systemName);

    /**
     * 根据系统id查询所关联的租户id
     *
     * @param systemId 系统id
     * @return {@code List<}{@link String}{@code >}
     */
    List<String> listTenantIdBySystemId(String systemId);

    /**
     * 分页查询租户应用列表
     *
     * @param tenantId 租户id
     * @param pageQuery 分页查询参数
     * @return {@code Page<}{@link TenantSystem}{@code >}
     */
    Y9Page<TenantSystem> pageByTenantId(String tenantId, Y9PageQuery pageQuery);

    /**
     * 保存租户应用实体类
     *
     * @param entity 租户应用实体类
     * @return {@link TenantSystem}
     */
    TenantSystem save(TenantSystem entity);

    /**
     * 保存租户租用的系统
     *
     * @param systemId 系统id
     * @param tenantId 租户id
     * @return {@link TenantSystem}
     */
    TenantSystem saveTenantSystem(String systemId, String tenantId);

    /**
     * 批量保存租户租用的系统
     *
     * @param systemIds 系统id数组
     * @param tenantId 租户id
     * @return {@code List<}{@link TenantSystem}{@code >}
     */
    List<TenantSystem> saveTenantSystems(String[] systemIds, String tenantId);

    TenantSystem getById(String id);
}
