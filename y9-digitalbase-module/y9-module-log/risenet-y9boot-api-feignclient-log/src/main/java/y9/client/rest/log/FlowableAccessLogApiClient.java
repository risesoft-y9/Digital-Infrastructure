package y9.client.rest.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.log.FlowableAccessLogApi;
import net.risesoft.model.log.FlowableAccessLog;
import net.risesoft.model.log.FlowableAccessLogQuery;
import net.risesoft.pojo.Y9Page;

/**
 * 访问日志组件
 *
 * @author qinman
 * @date 2025/05/22
 * @since 9.6.8
 */
@FeignClient(contextId = "FlowableAccessLogApiClient", name = "${y9.service.log.name:log}",
    url = "${y9.service.log.directUrl:}",
    path = "/${y9.service.log.name:server-log}/services/rest/v1/flowable/accessLog")
public interface FlowableAccessLogApiClient extends FlowableAccessLogApi {

    @Override
    Y9Page<FlowableAccessLog> search(@SpringQueryMap FlowableAccessLogQuery query, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

}