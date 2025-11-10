package net.risesoft.y9public.service.tenant;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.tenant.TenantApp;
import net.risesoft.pojo.Y9Page;
import net.risesoft.query.platform.TenantAppQuery;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9TenantAppService {

    /**
     * 根据appId删除对象
     *
     * @param appId 应用id
     */
    void deleteByAppId(String appId);

    /**
     * 禁用应用，（修改租用状态）
     *
     * @param tenantId 租户id
     * @param appId 应用id
     */
    void deleteByTenantIdAndAppId(String tenantId, String appId);

    /**
     * 按租户id和系统id取消租用系统下的所有应用
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     */
    void deleteByTenantIdAndSystemId(String tenantId, String systemId);

    /**
     * 根据id，获取租户应用
     *
     * @param id 租户应用id
     * @return 租户应用对象 或 null
     */
    Optional<TenantApp> findById(String id);

    /**
     * 根据id，获取租户应用
     *
     * @param id id
     * @return {@link TenantApp}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    TenantApp getById(String id);

    /**
     * 根据appId和tenantId，获取租户应用
     *
     * @param tenantId 租户id
     * @param appId 应用id
     * @param tenancy 是否租用状态
     * @return {@code Optional<}{@link TenantApp}{@code >}
     */
    Optional<TenantApp> findByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);

    /**
     * 根据租户id、系统id，获取已审核且还在租用的应用id列表
     *
     * @param systemId 系统id
     * @param tenantId 租户id
     * @param verify 是否已审核
     * @param tenancy 是否已租用
     * @return {@code Page<}{@link String}{@code >}
     */
    List<String> listAppIdBySystemIdAndTenantId(String systemId, String tenantId, Boolean verify, Boolean tenancy);

    /**
     * 根据租户id，获取已审核且还在租用的应用id列表
     *
     * @param tenantId 租户id
     * @param verify 是否已审核
     * @param tenancy 是否已租用
     * @return {@code Page<}{@link String}{@code >}
     */
    List<String> listAppIdByTenantId(String tenantId, Boolean verify, Boolean tenancy);

    /**
     * 根据租户Id，获取租户应用id列表
     *
     * @param tenantId 租户id
     * @param tenancy 是否已租用
     * @return {@code Page<}{@link String}{@code >}
     */
    List<String> listAppIdByTenantIdAndTenancy(String tenantId, Boolean tenancy);

    /**
     * 获取租户应用租用情况信息
     *
     * @param page page
     * @param rows rows
     * @param tenantAppQuery 搜索查询
     * @return {@code Page<}{@link TenantApp}{@code >}
     */
    Y9Page<TenantApp> page(Integer page, Integer rows, TenantAppQuery tenantAppQuery);

    /**
     * 租户租用应用，已租用系统的应用自动审核通过
     *
     * @param appId 应用id
     * @param tenantId 租户id
     * @param applyReason 申请原因
     * @return {@link TenantApp}
     */
    TenantApp save(String appId, String tenantId, String applyReason);

    /**
     * 保存租户应用
     *
     * @param y9TenantApp 租户应用对象
     * @return {@link TenantApp}
     */
    TenantApp save(TenantApp y9TenantApp);

    /**
     * 保存或更新
     *
     * @param appId 应用id
     * @param appName 应用名称
     */
    void saveOrUpdate(String appId, String appName);

}
