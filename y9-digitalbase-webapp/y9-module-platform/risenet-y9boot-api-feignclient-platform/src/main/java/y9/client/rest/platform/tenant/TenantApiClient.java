package y9.client.rest.platform.tenant;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.tenant.TenantApi;

/**
 * 租户管理员
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "TenantApiClient", name = "${y9.service.org.name:platform}", url = "${y9.service.org.directUrl:}",
    path = "/${y9.service.org.name:platform}/services/rest/v1/tenant", primary = false)
public interface TenantApiClient extends TenantApi {

}