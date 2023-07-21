package y9.client.platform.resource;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.resource.AppIconApi;
import net.risesoft.model.AppIcon;

/**
 * 图标库管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AppIconApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/appIcon")
public interface AppIconApiClient extends AppIconApi {

    /**
     * 查询所有图标
     *
     * @return List&lt;AppIcon&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllIcon")
    List<AppIcon> listAllIcon();

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return List&lt;AppIcon&gt;
     * @since 9.6.0
     */
    @Override
    @GetMapping("/searchAppIcon")
    List<AppIcon> searchAppIcon(@RequestParam("name") String name);
}
