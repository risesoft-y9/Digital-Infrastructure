package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.log.ClickedAppApi;

/**
 * 应用点击组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@FeignClient(contextId = "ClickedAppApiClient", name = "clickedApp", url = "${y9.common.logBaseUrl}",
    path = "/services/rest/v1/clickedApp")
public interface ClickedAppApiClient extends ClickedAppApi {

}
