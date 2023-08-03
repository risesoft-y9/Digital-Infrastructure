package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.permission.PositionRoleApi;

/**
 * 岗位角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionRoleApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/positionRole", primary = false)
public interface PositionRoleApiClient extends PositionRoleApi {

}
