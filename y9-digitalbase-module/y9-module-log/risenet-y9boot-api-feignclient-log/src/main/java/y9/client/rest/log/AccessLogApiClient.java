package y9.client.rest.log;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;

import net.risesoft.api.log.AccessLogApi;
import net.risesoft.model.log.AccessLog;
import net.risesoft.model.log.AccessLogQuery;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * 访问日志组件
 *
 * @author shidaobang
 * @date 2022/12/28
 * @since 9.6.0
 */
@FeignClient(contextId = "AccessLogApiClient", name = "${y9.service.log.name:log}",
    url = "${y9.service.log.directUrl:}", path = "/${y9.service.log.name:server-log}/services/rest/v1/accessLog")
public interface AccessLogApiClient extends AccessLogApi {

    @Override
    Y9Page<AccessLog> search(@SpringQueryMap AccessLogQuery accessLogQuery, @SpringQueryMap Y9PageQuery pageQuery);

}