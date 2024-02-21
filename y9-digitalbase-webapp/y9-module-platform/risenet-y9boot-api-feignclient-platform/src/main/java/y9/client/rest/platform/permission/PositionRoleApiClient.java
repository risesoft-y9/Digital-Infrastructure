package y9.client.rest.platform.permission;

import net.risesoft.api.platform.permission.PositionRoleApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 岗位角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionRoleApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/positionRole",
    primary = false)
public interface PositionRoleApiClient extends PositionRoleApi {

}
