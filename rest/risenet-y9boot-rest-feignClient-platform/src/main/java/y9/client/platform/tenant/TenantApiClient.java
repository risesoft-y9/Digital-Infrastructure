package y9.client.platform.tenant;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.tenant.TenantApi;
import net.risesoft.model.Tenant;

/**
 * 租户管理员
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "TenantApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/tenant")
public interface TenantApiClient extends TenantApi {

    /**
     * 根据租户id获取一个租户对象
     *
     * @param tenantId 租户id
     * @return Tenant 租户对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getById")
    Tenant getById(@RequestParam("tenantId") String tenantId);

    /**
     * 获取所有租户对象
     *
     * @return List&lt;Tenant&gt; 所有租户对象的集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllTenants")
    List<Tenant> listAllTenants();

    /**
     * 根据租户名，获取租户列表
     *
     * @param tenantName 租户名
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByName")
    List<Tenant> listByName(@RequestParam("tenantName") String tenantName);

    /**
     * 根据租户登录名称（租户英文名称），获取租户列表
     *
     * @param shortName 租户登录名称（租户英文名称）
     * @return List&lt;Tenant&gt; 租户对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByShortName")
    List<Tenant> listByShortName(@RequestParam("shortName") String shortName);

    /**
     * 获取指定租户类型的所有租户对象
     *
     * @param tenantType 租户类型： 0=用户，2=开发商，1=运维团队，3=普通租户
     * @return List&lt;Tenant&gt; 所有租户对象的集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByTenantType")
    List<Tenant> listByTenantType(@RequestParam("tenantType") Integer tenantType);

}