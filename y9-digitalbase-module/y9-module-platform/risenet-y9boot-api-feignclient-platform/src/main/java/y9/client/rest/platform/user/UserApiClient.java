package y9.client.rest.platform.user;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.user.UserApi;

/**
 * 用户 API
 *
 * @author shidaobang
 * @date 2025/06/16
 */
@FeignClient(contextId = "UserApiClient", name = "${y9.service.org.name:platform}",
    url = "${y9.service.org.directUrl:}", path = "/${y9.service.org.name:server-platform}/services/rest/v1/user",
    primary = false)
public interface UserApiClient extends UserApi {}
