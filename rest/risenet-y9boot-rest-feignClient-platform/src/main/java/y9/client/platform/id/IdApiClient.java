package y9.client.platform.id;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

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
@FeignClient(contextId = "IdApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/id")
public interface IdApiClient extends IdApi {

    /**
     * 获取snowflake id
     *
     * @return String id
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getNextId")
    String getNextId();

}
