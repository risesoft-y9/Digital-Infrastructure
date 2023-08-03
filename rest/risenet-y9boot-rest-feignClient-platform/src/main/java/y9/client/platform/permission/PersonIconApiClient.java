package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.permission.PersonIconApi;

/**
 * 人员图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonIconApiClient", name = "y9platform", url = "${y9.common.y9DigitalBaseUrl}",
    path = "/services/rest/personIcon")
public interface PersonIconApiClient extends PersonIconApi {

}
