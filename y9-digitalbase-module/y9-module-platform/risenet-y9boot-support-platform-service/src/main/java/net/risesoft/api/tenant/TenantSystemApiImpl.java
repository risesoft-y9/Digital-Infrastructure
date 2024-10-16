package net.risesoft.api.tenant;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.tenant.TenantSystemApi;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.Tenant;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 租户系统组件
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
@RequestMapping(value = "/services/rest/v1/tenantSystem", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TenantSystemApiImpl implements TenantSystemApi {

    private final Y9TenantSystemService y9TenantSystemService;

    /**
     * 根据租户id获取该租户租用的系统
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<System>>} 通用请求返回对象 - data是系统对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<System>> listSystemByTenantId(@RequestParam("tenantId") @NotBlank String tenantId) {
        List<Y9System> y9SystemList = y9TenantSystemService.listSystemByTenantId(tenantId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9SystemList, System.class));
    }

    /**
     * 根据租户id，获取租用的系统ID列表
     *
     * @param tenantId 租户ID
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data是系统id集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<String>> listSystemIdByTenantId(@RequestParam("tenantId") @NotBlank String tenantId) {
        return Y9Result.success(y9TenantSystemService.listSystemIdByTenantId(tenantId));
    }

    /**
     * 根据系统id查询租用了系统的租户
     *
     * @param systemId 系统id
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Tenant>> listTenantBySystemId(@RequestParam("systemId") @NotBlank String systemId) {
        List<Y9Tenant> y9TenantList = y9TenantSystemService.listTenantBySystemId(systemId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9TenantList, Tenant.class));
    }

    /**
     * 根据系统名查询租用了系统的租户
     *
     * @param systemName 系统名
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Tenant>> listTenantBySystemName(@RequestParam("systemName") @NotBlank String systemName) {
        List<Y9Tenant> y9TenantList = y9TenantSystemService.listTenantBySystemName(systemName);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9TenantList, Tenant.class));
    }

}
