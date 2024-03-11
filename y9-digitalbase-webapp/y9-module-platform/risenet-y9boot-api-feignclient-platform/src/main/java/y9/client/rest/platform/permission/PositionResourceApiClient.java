package y9.client.rest.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.permission.PositionResourceApi;

/**
 * 岗位资源管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionResourceApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/positionResource",
    primary = false)
public interface PositionResourceApiClient extends PositionResourceApi {

}
