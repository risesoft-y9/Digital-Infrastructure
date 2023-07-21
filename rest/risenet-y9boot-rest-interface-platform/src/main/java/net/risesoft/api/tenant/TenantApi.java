package net.risesoft.api.tenant;

import java.util.List;

import net.risesoft.model.Tenant;

/**
 * 租户管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface TenantApi {

    /**
     * 根据租户id获取租户对象
     *
     * @param tenantId 租户id
     * @return Tenant 租户对象
     * @since 9.6.0
     */
    Tenant getById(String tenantId);

    /**
     * 获取所有租户对象
     *
     * @return List&lt;Tenant&gt; 所有租户对象的集合
     * @since 9.6.0
     */
    List<Tenant> listAllTenants();

    /**
     * 根据租户名，获取租户列表
     *
     * @param tenantName 租户名
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    List<Tenant> listByName(String tenantName);

    /**
     * 根据租户登录名称（租户英文名称），获取租户列表
     *
     * @param shortName 租户登录名称（租户英文名称）
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    List<Tenant> listByShortName(String shortName);

    /**
     * 获取指定租户类型的所有租户对象
     *
     * @param tenantType 租户类型 {@link net.risesoft.enums.TenantTypeEnum}
     * @return List&lt;Tenant&gt; 所有租户对象的集合
     * @since 9.6.0
     */
    List<Tenant> listByTenantType(Integer tenantType);

}