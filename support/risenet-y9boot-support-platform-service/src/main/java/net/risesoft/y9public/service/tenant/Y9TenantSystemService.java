package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;

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
     * @return {@link Y9TenantSystem}
     */
    Optional<Y9TenantSystem> findById(String id);

    /**
     * 通过租户id和系统id
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     * @return {@link Y9TenantSystem}
     */
    Optional<Y9TenantSystem> getByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据系统唯一标示查找租户系统集合
     *
     * @param systemId 系统id
     * @return {@link List}<{@link Y9TenantSystem}>
     */
    List<Y9TenantSystem> listBySystemId(String systemId);

    /**
     * 根据租户id查询所关联的系统id列表
     *
     * @param tenantId 租户id
     * @return {@link List}<{@link String}>
     */
    List<String> listSystemIdByTenantId(String tenantId);

    /**
     * 根据系统id查询所关联的租户id
     *
     * @param systemId 系统id
     * @return {@link List}<{@link String}>
     */
    List<String> listTenantIdBySystemId(String systemId);

    /**
     * 分页查询租户应用列表
     *
     * @param tenantId 租户id
     * @param pageQuery 分页查询参数
     * @return {@link Page}<{@link Y9TenantSystem}>
     */
    Page<Y9TenantSystem> pageByTenantId(String tenantId, Y9PageQuery pageQuery);

    /**
     * 保存租户应用实体类
     *
     * @param entity 租户应用实体类
     * @return
     */
    Y9TenantSystem save(Y9TenantSystem entity);

    /**
     * 保存租户租用的系统
     *
     * @param systemId 系统id
     * @param tenantId 租户id
     * @return {@link Y9TenantSystem}
     */
    Y9TenantSystem saveTenantSystem(String systemId, String tenantId);

    /**
     * 批量保存租户租用的系统
     *
     * @param systemIds 系统id数组
     * @param tenantId 租户id
     * @return {@link List}<{@link Y9TenantSystem}>
     */
    List<Y9TenantSystem> saveTenantSystems(String[] systemIds, String tenantId);

    List<Y9Tenant> listTenantBySystemId(String systemId);

    List<Y9Tenant> listTenantBySystemName(String systemName);

    List<Y9System> listSystemByTenantId(String tenantId);

}
