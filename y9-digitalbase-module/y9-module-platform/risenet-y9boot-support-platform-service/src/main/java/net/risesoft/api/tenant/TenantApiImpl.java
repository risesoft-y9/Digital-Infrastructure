package net.risesoft.api.tenant;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.model.platform.Tenant;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
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
@RestController
@RequestMapping(value = "/services/rest/v1/tenant", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TenantApiImpl implements TenantApi {

    private final Y9TenantService y9TenantService;

    /**
     * 根据租户名，获取租户
     *
     * @param tenantName 租户名
     * @return {@code Y9Result<Tenant>} 通用请求返回对象 - data是租户对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Tenant> findByName(@RequestParam("tenantName") @NotBlank String tenantName) {
        Y9Tenant y9Tenant = y9TenantService.findByTenantName(tenantName).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Tenant, Tenant.class));
    }

    /**
     * 根据租户登录名称（租户英文名称），获取租户
     *
     * @param shortName 租户登录名称（租户英文名称）
     * @return {@code Y9Result<Tenant>} 通用请求返回对象 - data 是租户对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Tenant> findByShortName(@RequestParam("shortName") @NotBlank String shortName) {
        Y9Tenant y9Tenant = y9TenantService.findByShortName(shortName).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Tenant, Tenant.class));
    }

    /**
     * 根据租户id获取一个租户对象
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<Tenant>} 通用请求返回对象 - data 是租户对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Tenant> getById(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9Tenant y9Tenant = y9TenantService.getById(tenantId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Tenant, Tenant.class));
    }

    /**
     * 获取所有租户对象
     *
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Tenant>> listAllTenants() {
        List<Y9Tenant> tenantEntityList = y9TenantService.listAll();
        return Y9Result.success(Y9ModelConvertUtil.convert(tenantEntityList, Tenant.class));
    }

}
