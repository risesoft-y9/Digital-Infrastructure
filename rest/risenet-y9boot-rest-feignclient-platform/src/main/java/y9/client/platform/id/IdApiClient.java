package y9.client.platform.id;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.id.IdApi;

/**
 * 唯一标识组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "IdApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/v1/id", primary = false)
public interface IdApiClient extends IdApi {

}
