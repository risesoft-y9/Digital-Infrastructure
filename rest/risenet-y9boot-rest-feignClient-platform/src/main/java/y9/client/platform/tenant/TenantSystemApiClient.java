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
@FeignClient(contextId = "TenantSystemApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/tenantSystem")
public interface TenantSystemApiClient extends TenantSystemApi {}
