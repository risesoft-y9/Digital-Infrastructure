package net.risesoft.api.tenant;

import java.util.List;

import net.risesoft.model.System;
import net.risesoft.model.Tenant;

/**
 * 租户系统组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface TenantSystemApi {

    /**
     * 根据租户id获取该租户租用的系统
     *
     * @param tenantId 租户id
     * @return List&lt;AdminSystem&gt; 系统对象集合
     * @since 9.6.0
     */
    List<System> listSystemByTenantId(String tenantId);

    /**
     * 根据租户id，获取租用的系统id列表
     *
     * @param tenantId 租户ID
     * @return List&lt;String&gt; 系统id列表
     * @since 9.6.0
     */
    List<String> listSystemIdByTenantId(String tenantId);

    /**
     * 根据系统id,获取租用该系统的租户列表
     *
     * @param systemId 系统id
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    List<Tenant> listTenantBySystemId(String systemId);

    /**
     * 根据系统名,获取租用该系统的租户列表
     *
     * @param systemName 系统名
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    List<Tenant> listTenantBySystemName(String systemName);
}
