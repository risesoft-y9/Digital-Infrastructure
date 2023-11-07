package net.risesoft.api.tenant;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.System;
import net.risesoft.model.Tenant;
import net.risesoft.pojo.Y9Result;

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
     * @return {@code Y9Result<List<System>>} 通用请求返回对象 - data是系统对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSystemByTenantId")
    Y9Result<List<System>> listSystemByTenantId(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据租户id，获取租用的系统ID列表
     *
     * @param tenantId 租户ID
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data是系统id集合
     * @since 9.6.0
     */
    @GetMapping("/listSystemIdByTenantId")
    Y9Result<List<String>> listSystemIdByTenantId(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据系统id查询租用了系统的租户
     *
     * @param systemId 系统id
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listTenantBySystemId")
    Y9Result<List<Tenant>> listTenantBySystemId(@RequestParam("systemId") @NotBlank String systemId);

    /**
     * 根据系统名查询租用了系统的租户
     *
     * @param systemName 系统名
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listTenantBySystemName")
    Y9Result<List<Tenant>> listTenantBySystemName(@RequestParam("systemName") @NotBlank String systemName);
}
