package y9.client.platform.org;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.org.PositionApi;

/**
 * 岗位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/position")
public interface PositionApiClient extends PositionApi {

}
