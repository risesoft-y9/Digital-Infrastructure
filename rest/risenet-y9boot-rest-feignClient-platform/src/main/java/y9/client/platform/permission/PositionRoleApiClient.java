package y9.client.platform.permission;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.permission.PositionRoleApi;

/**
 * 岗位角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "PositionRoleApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/positionRole", primary = false)
public interface PositionRoleApiClient extends PositionRoleApi {

    /**
     * 判断岗位是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("customId") String customId);

}
