package y9.client.rest.platform.org;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.org.GroupApi;

/**
 * 用户组服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "GroupApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/group",
    primary = false)
public interface GroupApiClient extends GroupApi {

}