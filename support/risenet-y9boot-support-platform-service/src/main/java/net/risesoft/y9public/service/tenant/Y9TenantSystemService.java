package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

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
     * 根据系统id，获取该系统的租用数量
     *
     * @param systemId 系统id
     * @return long
     */
    long countBySystemId(String systemId);

    /**
     * 根据租户id查询应用数量
     *
     * @param tenantId 租户id
     * @return long
     */
    long countByTenantId(String tenantId);

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
     * 根据租户id与appid查询该用户是否有该应用
     *
     * @param tenantId 承租者id
     * @param systemId 系统标识
     * @return long
     */
    long existByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据id查找租户应用
     *
     * @param id 租户应用主键id
     * @return {@link Y9TenantSystem}
     */
    Y9TenantSystem findById(String id);

    /**
     * 通过租户id和系统id
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     * @return {@link Y9TenantSystem}
     */
    Optional<Y9TenantSystem> getByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据tenantId与systemId查询dataSourceId
     *
     * @param tenantId 承租者id
     * @param systemId 系统标识
     * @return {@link String}
     */
    String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据tenantId与appId查询id
     *
     * @param tenantId 承租者id
     * @param systemId 系统标识
     * @return {@link String}
     */
    String getIdByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据系统唯一标示查找租户系统集合
     *
     * @param systemId 系统id
     * @return {@link List}<{@link Y9TenantSystem}>
     */
    List<Y9TenantSystem> listBySystemId(String systemId);

    /**
     * 根据租户数据源查找
     *
     * @param tenantDataSource 租户数据源
     * @return {@link List}<{@link Y9TenantSystem}>
     */
    List<Y9TenantSystem> listByTenantDataSource(String tenantDataSource);

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
     * @param page 页数
     * @param rows 每页的行数
     * @param tenantId 租户id
     * @return {@link Page}<{@link Y9TenantSystem}>
     */
    Page<Y9TenantSystem> pageByTenantId(int page, int rows, String tenantId);

    /**
     * 保存租户应用实体类
     *
     * @param entity 租户应用实体类
     */
    void save(Y9TenantSystem entity);

    /**
     * 新增租户系统,新增的时候查找该系统的最新版本的全量sql文件，如果存在就初始化，不存在就提醒
     *
     * @param y9TenantSystem 租户系统对象
     * @param y9System 系统对象
     * @param tenantId 租户id
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> saveAndInitializedTenantSystem(Y9TenantSystem y9TenantSystem, Y9System y9System,
        String tenantId);

    /**
     * 保存租户租用的系统
     *
     * @param systemId 系统id
     * @param tenantId 租户id
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> saveTenantSystem(String systemId, String tenantId);

    /**
     * 批量保存租户租用的系统
     *
     * @param systemIds 系统id数组
     * @param tenantId 租户id
     * @return {@link List}<{@link Map}<{@link String}, {@link Object}>>
     * @throws Exception 异常
     */
    List<Map<String, Object>> saveTenantSystems(String systemIds[], String tenantId) throws Exception;

    List<Y9Tenant> listTenantBySystemId(String systemId);

    List<Y9Tenant> listTenantBySystemName(String systemName);

    List<Y9System> listSystemByTenantId(String tenantId);

    /**
     * 租户租用系统，发送消息通知系统重新加载数据源
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     */
    void registerTenantSystem(String tenantId, String systemId);
}
