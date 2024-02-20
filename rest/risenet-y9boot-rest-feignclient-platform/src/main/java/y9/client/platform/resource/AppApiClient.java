package y9.client.platform.resource;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.resource.AppApi;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AppApiClient", name = "${y9.service.org.name:platform}", url = "${y9.service.org.directUrl:}",
    path = "/${y9.service.org.name:platform}/services/rest/v1/app", primary = false)
public interface AppApiClient extends AppApi {

}
