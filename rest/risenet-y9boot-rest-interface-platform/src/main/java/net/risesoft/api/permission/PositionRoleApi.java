package net.risesoft.api.permission;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 岗位角色接口
 *
 * @author shidaobang
 * @date 2022/11/11
 * @since 9.6.0
 */
@Validated
public interface PositionRoleApi {

    /**
     * 判断岗位是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return Boolean
     * @since 9.6.0
     */
    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId);

}
