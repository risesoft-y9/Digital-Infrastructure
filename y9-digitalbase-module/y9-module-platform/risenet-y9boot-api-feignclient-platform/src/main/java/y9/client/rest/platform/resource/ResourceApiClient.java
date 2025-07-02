package y9.client.rest.platform.resource;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.resource.ResourceApi;

/**
 * 资源管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "ResourceApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/resource",
    primary = false)
public interface ResourceApiClient extends ResourceApi {

}
