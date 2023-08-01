package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.permission.PersonRoleApi;

/**
 * 人员角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PersonRoleApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/personRole", primary = false)
public interface PersonRoleApiClient extends PersonRoleApi {

}
