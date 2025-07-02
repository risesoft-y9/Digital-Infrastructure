package y9.client.rest.platform.resource;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.resource.SystemApi;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "SystemApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/system",
    primary = false)
public interface SystemApiClient extends SystemApi {

}
