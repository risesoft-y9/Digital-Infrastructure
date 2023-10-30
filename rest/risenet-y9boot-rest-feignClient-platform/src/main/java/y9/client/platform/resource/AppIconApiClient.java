package y9.client.platform.resource;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.resource.AppIconApi;

/**
 * 图标库管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AppIconApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/v1/appIcon")
public interface AppIconApiClient extends AppIconApi {

}
