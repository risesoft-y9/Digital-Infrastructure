package y9.client.rest.platform.permission;

import net.risesoft.api.platform.permission.RoleApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "RoleApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/role",
    primary = false)
public interface RoleApiClient extends RoleApi {}
