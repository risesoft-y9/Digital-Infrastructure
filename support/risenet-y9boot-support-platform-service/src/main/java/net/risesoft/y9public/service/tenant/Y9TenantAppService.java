package net.risesoft.y9public.service.tenant;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import net.risesoft.y9public.entity.tenant.Y9TenantApp;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9TenantAppService {

    /**
     * 根据租户id和系统id计数
     *
     * @param tenantId 租户id
     * @param systemId 系统id
     * @return long 租户应用数
     */
    long countByTenantIdAndSystemId(String tenantId, String systemId);

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
     * @return int
     */
    int deleteByTenantIdAndAppId(String tenantId, String appId);

    /**
     * 根据id，获取租户应用
     *
     * @param id 租户应用id
     * @return 租户应用对象 或 null
     */
    Y9TenantApp findById(String id);

    /**
     * 根据appId和tenantId，获取租户应用
     *
     * @param tenantId 租户id
     * @param appId 应用id
     */
    Optional<Y9TenantApp> getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy);

    /**
     * 根据租户id，获取已审核且还在租用的应用id列表
     *
     * @param tenantId 租户id
     * @param verify 是否已审核
     * @param tenancy 是否已租用
     * @return {@link List}<{@link String}>
     */
    List<String> listAppIdByTenantId(String tenantId, Boolean verify, Boolean tenancy);

    /**
     * 根据租户Id，获取租户应用id列表
     *
     * @param tenantId 租户id
     * @param tenancy 是否已租用
     * @return {@link List}<{@link String}>
     */
    List<String> listAppIdByTenantIdAndTenancy(String tenantId, Boolean tenancy);

    /**
     * 根据应用Id，查询租户应用列表
     *
     * @param appId 应用id
     * @param tenancy 是否已租用
     * @return {@link List}<{@link Y9TenantApp}>
     */
    List<Y9TenantApp> listByAppIdAndTenancy(String appId, Boolean tenancy);

    /**
     * 根据租户id，审核状态和租用状态，获取租户应用列表
     *
     * @param tenantId 租户id
     * @param verify 是否已审核
     * @param tenancy 是否已租用
     * @return {@link List}<{@link Y9TenantApp}>
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
     * @return {@link Page}<{@link Y9TenantApp}>
     */
    Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime,
        String verifyTime, Boolean tenancy, String systemId);

    /**
     * 获取租户应用租用情况信息
     *
     * @param page page
     * @param rows rows
     * @param verify 审核状态
     * @param tenantName 租户名称
     * @param createTime 创建时间
     * @param verifyTime 审核时间
     * @param tenancy 租户状态
     * @param appName 应用名称
     * @param systemIds 管理的应用ids
     * @return {@link Page}<{@link Y9TenantApp}>
     */
    Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime,
        String verifyTime, Boolean tenancy, String appName, String systemIds);

    /**
     * 根据系统id，获取应用租户详情
     *
     * @param page 页数
     * @param rows 每页的行数
     * @param systemId 系统id
     * @return {@link Page}<{@link Y9TenantApp}>
     */
    Page<Y9TenantApp> pageBySystemId(int page, int rows, String systemId);

    /**
     * 根据tenantId获取租户应用分页列表
     *
     * @param page 页数
     * @param rows 每页的行数
     * @param tenantId 租户id
     * @return {@link Page}<{@link Y9TenantApp}>
     */
    Page<Y9TenantApp> pageByTenantId(int page, int rows, String tenantId);

    /**
     * 租户租用应用，已租用系统的应用自动审核通过
     *
     * @param appId 应用id
     * @param tenantId 租户id
     * @param applyName 申请人
     * @param applyReason 申请原因
     * @return {@link Y9TenantApp}
     */
    Y9TenantApp save(String appId, String tenantId, String applyName, String applyReason);

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
