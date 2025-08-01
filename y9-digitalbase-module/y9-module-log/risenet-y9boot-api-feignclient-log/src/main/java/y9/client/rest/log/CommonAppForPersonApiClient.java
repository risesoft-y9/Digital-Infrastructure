package y9.client.rest.log;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.log.CommonAppForPersonApi;

/**
 * 个人常用应用组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "CommonAppForPersonApiClient", name = "${y9.service.log.name:log}",
    url = "${y9.service.log.directUrl:}", path = "/${y9.service.log.name:server-log}/services/rest/v1/commonApp")
public interface CommonAppForPersonApiClient extends CommonAppForPersonApi {

}
