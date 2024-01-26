package y9.client.log;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.log.AccessLogApi;

/**
 * 访问日志组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "AccessLogApiClient", name = "log", url = "${y9.common.logBaseUrl}",
    path = "/services/rest/v1/accessLog")
public interface AccessLogApiClient extends AccessLogApi {

}