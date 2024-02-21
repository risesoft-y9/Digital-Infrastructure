package y9.client.rest.platform.resource;

import net.risesoft.api.platform.resource.AppIconApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 图标库管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AppIconApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/appIcon",
    primary = false)
public interface AppIconApiClient extends AppIconApi {

}
