package net.risesoft.api.v0.tenant;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.tenant.TenantApi;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.y9public.service.tenant.Y9TenantService;

/**
 * 租户管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0TenantApiImpl")
@RequestMapping(value = "/services/rest/tenant", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class TenantApiImpl implements TenantApi {

    private final Y9TenantService y9TenantService;

    /**
     * 根据租户名，获取租户列表
     *
     * @param tenantName 租户名
     * @return {@code Tenant} 租户对象集合
     * @since 9.6.0
     */
    @Override
    public Tenant findByName(@RequestParam("tenantName") @NotBlank String tenantName) {
        return y9TenantService.findByTenantName(tenantName).orElse(null);
    }

    /**
     * 根据租户登录名称（租户英文名称），获取租户列表
     *
     * @param shortName 租户登录名称（租户英文名称）
     * @return {@code Tenant} 租户对象集合
     * @since 9.6.0
     */
    @Override
    public Tenant findByShortName(@RequestParam("shortName") @NotBlank String shortName) {
        return y9TenantService.findByShortName(shortName).orElse(null);
    }

    /**
     * 根据租户id获取租户对象
     *
     * @param tenantId 租户id
     * @return Tenant 租户对象
     * @since 9.6.0
     */
    @Override
    public Tenant getById(@RequestParam("tenantId") @NotBlank String tenantId) {
        return y9TenantService.getById(tenantId);
    }

    /**
     * 获取所有租户对象
     *
     * @return {@code List<Tenant>}所有租户对象的集合
     * @since 9.6.0
     */
    @Override
    public List<Tenant> listAllTenants() {
        return y9TenantService.listAll();
    }

    /**
     * 获取指定租户类型的所有租户对象
     *
     * @return {@code List<Tenant>} 所有租户对象的集合
     * @since 9.6.0
     */
    @Override
    public List<Tenant> listByTenantType() {
        return y9TenantService.listAll();
    }

}
