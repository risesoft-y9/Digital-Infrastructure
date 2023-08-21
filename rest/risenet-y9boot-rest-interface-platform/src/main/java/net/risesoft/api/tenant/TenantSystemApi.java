package net.risesoft.api.tenant;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@Validated
public interface TenantSystemApi {

    /**
     * 根据租户id获取该租户租用的系统
     *
     * @param tenantId 租户id
     * @return List&lt;AdminSystem&gt; 系统对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSystemByTenantId")
    List<System> listSystemByTenantId(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据租户id，获取租用的系统ID列表
     *
     * @param tenantId 租户ID
     * @return List&lt;String&gt; 系统id列表
     * @since 9.6.0
     */
    @GetMapping("/listSystemIdByTenantId")
    List<String> listSystemIdByTenantId(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据系统查询该系统被哪儿些租户租用了
     *
     * @param systemId 系统id
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listTenantBySystemId")
    List<Tenant> listTenantBySystemId(@RequestParam("systemId") @NotBlank String systemId);

    /**
     * 根据系统名该系统被哪些租户租用了
     *
     * @param systemName 系统名
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listTenantBySystemName")
    List<Tenant> listTenantBySystemName(@RequestParam("systemName") @NotBlank String systemName);
}
