package net.risesoft.api.permission;

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
     * @return Boolean 是否拥有角色
     * @since 9.6.0
     */
    Boolean hasRole(String tenantId, String positionId, String customId);

}
