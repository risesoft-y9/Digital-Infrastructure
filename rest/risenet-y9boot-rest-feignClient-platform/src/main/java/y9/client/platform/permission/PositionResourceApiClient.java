package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.permission.PositionResourceApi;

/**
 * 岗位资源管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionResourceApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/positionResource")
public interface PositionResourceApiClient extends PositionResourceApi {

}
