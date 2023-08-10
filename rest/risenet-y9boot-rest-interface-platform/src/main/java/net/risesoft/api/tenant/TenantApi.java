package net.risesoft.api.tenant;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@Validated
public interface TenantApi {

    /**
     * 根据租户id获取一个租户对象
     *
     * @param tenantId 租户id
     * @return Tenant 租户对象
     * @since 9.6.0
     */
    @GetMapping("/getById")
    Tenant getById(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 获取所有租户对象
     *
     * @return List&lt;Tenant&gt; 所有租户对象的集合
     * @since 9.6.0
     */
    @GetMapping("/listAllTenants")
    List<Tenant> listAllTenants();

    /**
     * 根据租户名，获取租户列表
     *
     * @param tenantName 租户名
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByName")
    List<Tenant> listByName(@RequestParam("tenantName") @NotBlank String tenantName);

    /**
     * 根据租户登录名称（租户英文名称），获取租户列表
     *
     * @param shortName 租户登录名称（租户英文名称）
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByShortName")
    List<Tenant> listByShortName(@RequestParam("shortName") @NotBlank String shortName);

    /**
     * 获取指定租户类型的所有租户对象
     *
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return List&lt;Tenant&gt; 所有租户对象的集合
     * @since 9.6.0
     */
    @GetMapping("/listByTenantType")
    List<Tenant> listByTenantType(@RequestParam("tenantType") Integer tenantType);

}