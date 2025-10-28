package net.risesoft.y9public.service.tenant;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.query.platform.TenantAppQuery;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;

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
    Optional<Y9TenantApp> findById(String id);

    /**
     * 根据id，获取租户应用
     *
     * @param id id
     * @return {@link Y9TenantApp}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9TenantApp getById(String id);

    /**
     * 根据appId和tenantId，获取租户应用
     *
     * @param tenantId 租户id
     * @param appId 应用id
     * @param tenancy 是否租用状态
     * @return {@code Optional<}{@link Y9TenantApp}{@code >}
     */
    Optional<Y9TenantApp> getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);

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
     * 根据租户id，审核状态和租用状态，获取租户应用列表
     *
     * @param tenantId 租户id
     * @param verify 是否已审核
     * @param tenancy 是否已租用
     * @return {@code Page<}{@link Y9TenantApp}{@code >}
     */
    List<Y9TenantApp> listByTenantIdAndTenancy(String tenantId, Boolean verify, Boolean tenancy);

    /**
     * 根据搜索条件，获取租户应用列表分页信息
     *
     * @param page 页数
     * @param rows 每页的行数
     * @param verify 是否已审核
     * @param tenantName 租户名
     * @param createTime 创建时间
     * @param verifyTime 审核事件
     * @param tenancy 是否已租用
     * @param systemId 系统id
     * @return {@code Page<}{@link Y9TenantApp}{@code >}
     */
    Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime,
        String verifyTime, Boolean tenancy, String systemId);

    /**
     * 获取租户应用租用情况信息
     *
     * @param page page
     * @param rows rows
     * @param tenantAppQuery 搜索查询
     * @return {@code Page<}{@link Y9TenantApp}{@code >}
     */
    Page<Y9TenantApp> page(Integer page, Integer rows, TenantAppQuery tenantAppQuery);

    /**
     * 租户租用应用，已租用系统的应用自动审核通过
     *
     * @param appId 应用id
     * @param tenantId 租户id
     * @param applyReason 申请原因
     * @return {@link Y9TenantApp}
     */
    Y9TenantApp save(String appId, String tenantId, String applyReason);

    /**
     * 保存租户应用
     *
     * @param y9TenantApp 租户应用对象
     * @return {@link Y9TenantApp}
     */
    Y9TenantApp save(Y9TenantApp y9TenantApp);

    /**
     * 保存或更新
     *
     * @param appId 应用id
     * @param appName 应用名称
     */
    void saveOrUpdate(String appId, String appName);

    /**
     * 根据id，修改租用状态
     *
     * @param tenancy 是否已租用
     * @param deletedName 删除名称
     * @param deletedTime 删除时间
     * @param appId 应用id
     * @param tenantId 租户id
     * @param currentTenancy 当前是否已租用
     * @return int
     */
    int updateByAppIdAndTenantId(Boolean tenancy, String deletedName, Date deletedTime, String appId, String tenantId,
        Boolean currentTenancy);

    /**
     * 审核租户应用
     *
     * @param y9TenantApp 租户应用
     * @param reason 审核理由
     * @return {@link Y9TenantApp}
     */
    Y9TenantApp verify(Y9TenantApp y9TenantApp, String reason);
}
