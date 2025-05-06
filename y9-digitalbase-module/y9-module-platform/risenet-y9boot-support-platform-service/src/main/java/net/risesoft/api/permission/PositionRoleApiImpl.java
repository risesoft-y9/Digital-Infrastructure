package net.risesoft.api.permission;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.entity.Y9Position;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.identity.Y9PositionToRoleService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 岗位角色组件
 *
 * @author shidaobang
 * @date 2022/11/11
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/positionRole", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PositionRoleApiImpl implements PositionRoleApi {

    private final Y9PositionToRoleService y9PositionToRoleService;

    /**
     * 根据岗位id判断该岗位是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param positionId 岗位id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("positionId") @NotBlank String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionToRoleService.hasPublicRole(positionId, roleName));
    }

    /**
     * 判断岗位是否拥有角色
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param positionId 岗位id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("positionId") @NotBlank String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionToRoleService.hasRole(positionId, roleId));
    }

    /**
     * 根据岗位id判断该岗位是否拥有 roleName 这个角色
     *
     * @param tenantId 租户id
     * @param systemName 系统标识
     * @param properties 角色扩展属性
     * @param roleName 角色名称
     * @param positionId 岗位id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("positionId") @NotBlank String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionToRoleService.hasRole(positionId, systemName, roleName, properties));
    }

    /**
     * 判断岗位是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param customId 自定义id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasRoleByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId, @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PositionToRoleService.hasRoleByCustomId(positionId, customId));
    }

    /**
     * 获取拥有角色的所有岗位（不包含禁用）集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<Position>> listPositionsByRoleId(String tenantId, String roleId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PersonList = y9PositionToRoleService.listPositionsByRoleId(roleId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Position.class));
    }

    /**
     * 获取岗位所拥有的角色集合
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是角色集合
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<Role>> listRolesByPersonId(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Role> y9RoleList = y9PositionToRoleService.listRolesByPositionId(positionId);
        List<Role> roleList = new ArrayList<>();
        for (Y9Role y9Role : y9RoleList) {
            roleList.add(ModelConvertUtil.y9RoleToRole(y9Role));
        }
        return Y9Result.success(roleList);
    }
}
