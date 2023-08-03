package y9.client.platform.log;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.log.CommonAppForPersonApi;

/**
 * 个人常用应用组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "CommonAppForPersonApiClient", name = "commonAppForPersonApi", url = "${y9.common.logBaseUrl}",
    path = "/services/rest/commonapp")
public interface CommonAppForPersonApiClient extends CommonAppForPersonApi {

}
