package y9.client.rest.platform.permission.cache;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.permission.cache.PersonRoleApi;

/**
 * 人员角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonRoleApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/personRole",
    primary = false)
public interface PersonRoleApiClient extends PersonRoleApi {

}
