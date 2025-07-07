package y9.client.rest.log;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.log.ClickedAppApi;

/**
 * 应用点击组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@FeignClient(contextId = "ClickedAppApiClient", name = "${y9.service.log.name:log}",
    url = "${y9.service.log.directUrl:}", path = "/${y9.service.log.name:server-log}/services/rest/v1/clickedApp")
public interface ClickedAppApiClient extends ClickedAppApi {

}
