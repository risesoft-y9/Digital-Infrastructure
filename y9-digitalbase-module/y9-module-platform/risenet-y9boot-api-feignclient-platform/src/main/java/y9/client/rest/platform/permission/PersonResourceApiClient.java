package y9.client.rest.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.permission.PersonResourceApi;

/**
 * 人员资源权限查看组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonResourceApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/personResource",
    primary = false)
public interface PersonResourceApiClient extends PersonResourceApi {

}
