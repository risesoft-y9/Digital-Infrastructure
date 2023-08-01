package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.permission.RoleApi;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "RoleApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/role", primary = false)
public interface RoleApiClient extends RoleApi {
}
