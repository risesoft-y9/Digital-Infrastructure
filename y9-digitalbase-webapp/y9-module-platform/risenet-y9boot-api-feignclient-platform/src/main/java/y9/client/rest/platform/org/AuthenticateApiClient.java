package y9.client.rest.platform.org;

import net.risesoft.api.platform.org.AuthenticateApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 用户身份验证服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AuthenticateApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:platform}/services/rest/v1/auth",
    primary = false)
public interface AuthenticateApiClient extends AuthenticateApi {

}