package net.risesoft.api.tenant;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.Tenant;
import net.risesoft.pojo.Y9Result;

/**
 * 租户管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface TenantApi {

    /**
     * 根据租户名，获取租户
     *
     * @param tenantName 租户名
     * @return {@code Y9Result<Tenant>} 通用请求返回对象 - data是租户对象
     * @since 9.6.0
     */
    @GetMapping("/findByName")
    Y9Result<Tenant> findByName(@RequestParam("tenantName") @NotBlank String tenantName);

    /**
     * 根据租户登录名称（租户英文名称），获取租户
     *
     * @param shortName 租户登录名称（租户英文名称）
     * @return {@code Y9Result<Tenant>} 通用请求返回对象 - data 是租户对象
     * @since 9.6.0
     */
    @GetMapping("/findByShortName")
    Y9Result<Tenant> findByShortName(@RequestParam("shortName") @NotBlank String shortName);

    /**
     * 根据租户id获取一个租户对象
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<Tenant>} 通用请求返回对象 - data 是租户对象
     * @since 9.6.0
     */
    @GetMapping("/getById")
    Y9Result<Tenant> getById(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 获取所有租户对象
     *
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllTenants")
    Y9Result<List<Tenant>> listAllTenants();

    /**
     * 获取指定租户类型的所有租户对象
     *
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return {@code Y9Result<List<Tenant>>} 通用请求返回对象 - data是租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByTenantType")
    Y9Result<List<Tenant>> listByTenantType(@RequestParam("tenantType") Integer tenantType);

}