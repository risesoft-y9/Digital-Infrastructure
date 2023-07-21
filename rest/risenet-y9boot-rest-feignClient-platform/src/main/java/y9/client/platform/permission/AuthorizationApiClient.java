package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.permission.AuthorizationApi;
import net.risesoft.enums.AuthorityEnum;

/**
 * 权限管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "AuthorizationApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/authorization")
public interface AuthorizationApiClient extends AuthorizationApi {

    /**
     * 保存授权信息
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param resourceId 资源id
     * @param roleId 角色id
     * @param authority 操作类型 {@link AuthorityEnum}
     * @since 9.6.0
     */
    @Override
    @PostMapping("/save")
    void save(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("resourceId") String resourceId, @RequestParam("roleId") String roleId, @RequestParam("authority") Integer authority);

}
