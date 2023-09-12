package net.risesoft.api.permission;

import javax.validation.constraints.NotBlank;

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
     * 根据岗位id判断该岗位是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param positionId 岗位id
     * @return boolean
     * @since 9.6.0
     */
    @GetMapping("/hasPublicRole")
    Boolean hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("positionId") @NotBlank String positionId);

    /**
     * 判断岗位是否拥有角色
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param positionId 岗位id
     * @return {@link Boolean}
     * @since 9.6.0
     */
    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("positionId") @NotBlank String positionId);

    /**
     * 判断岗位是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return Boolean
     * @since 9.6.0
     */
    @GetMapping("/hasRole2")
    Boolean hasRoleByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId);

    /**
     * 根据岗位id判断该岗位是否拥有 roleName 这个角色
     *
     * @param tenantId 租户id
     * @param systemName 系统标识
     * @param properties 角色扩展属性
     * @param roleName 角色名称
     * @param positionId 岗位id
     * @return Boolean 是否拥有
     * @since 9.6.0
     */
    @GetMapping("/hasRole3")
    Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("positionId") @NotBlank String positionId);
}
