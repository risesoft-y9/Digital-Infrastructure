package net.risesoft.api.permission;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 岗位角色接口
 *
 * @author shidaobang
 * @date 2022/11/11
 * @since 9.6.0
 */
public interface PositionRoleApi {

    /**
     * 判断岗位是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return
     * @since 9.6.0
     */
    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("customId") String customId);

}
