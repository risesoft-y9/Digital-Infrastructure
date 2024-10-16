package y9.client.rest.platform.id;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.id.IdApi;

/**
 * 唯一标识组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "IdApiClient", name = "${y9.service.org.name:platform}", url = "${y9.service.org.directUrl:}",
    path = "/${y9.service.org.name:platform}/services/rest/v1/id", primary = false)
public interface IdApiClient extends IdApi {

}
