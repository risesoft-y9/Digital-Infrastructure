package y9.client.rest.platform.org;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.platform.org.JobApi;

/**
 * 职位服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @author shidaobang
 * @date 2025/3/19
 * @since 9.6.8
 */
@FeignClient(contextId = "JobApiClient", name = "${y9.service.org.name:platform}", url = "${y9.service.org.directUrl:}",
    path = "/${y9.service.org.name:platform}/services/rest/v1/job", primary = false)
public interface JobApiClient extends JobApi {

}
