package y9.client.platform.tenant;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.tenant.TenantSystemApi;

/**
 * 租户系统
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "TenantSystemApiClient", name = "${y9.service.org.name:platform}", url = "${y9.service.org.directUrl:}",
    path = "/${y9.service.org.name:platform}/services/rest/v1/tenantSystem", primary = false)
public interface TenantSystemApiClient extends TenantSystemApi {}
