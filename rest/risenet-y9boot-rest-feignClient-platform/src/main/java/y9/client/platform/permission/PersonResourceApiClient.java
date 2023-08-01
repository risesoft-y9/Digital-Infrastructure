package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.permission.PersonResourceApi;

/**
 * 人员资源权限查看组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonResourceApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/personResource")
public interface PersonResourceApiClient extends PersonResourceApi {

}
