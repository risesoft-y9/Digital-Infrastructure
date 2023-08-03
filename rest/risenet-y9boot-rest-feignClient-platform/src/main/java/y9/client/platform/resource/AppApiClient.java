package y9.client.platform.resource;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.resource.AppApi;
import net.risesoft.model.App;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AppApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/app")
public interface AppApiClient extends AppApi {

    /**
     * 保存应用
     *
     * @param app 应用实体类
     * @param systemEntityId 系统id
     * @return App
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveIsvApp")
    App saveIsvApp(@SpringQueryMap App app, @RequestParam("systemId") String systemEntityId);
}
