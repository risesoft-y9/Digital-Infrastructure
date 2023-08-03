package y9.client.platform.customgroup;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.customgroup.CustomGroupApi;
import net.risesoft.model.CustomGroup;

/**
 * 自定义用户组
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "CustomGroupApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}",
    path = "/services/rest/customGroup")
public interface CustomGroupApiClient extends CustomGroupApi {

    /**
     * 保存用户组
     *
     * @param tenantId 租户id
     * @param customGroup 自定义用户组
     * @return CustomGroup 用户组对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveCustomGroup")
    CustomGroup saveCustomGroup(@RequestParam("tenantId") String tenantId, @SpringQueryMap CustomGroup customGroup);
}