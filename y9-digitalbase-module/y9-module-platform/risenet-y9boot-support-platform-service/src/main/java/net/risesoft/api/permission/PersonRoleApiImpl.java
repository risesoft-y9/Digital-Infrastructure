package net.risesoft.api.permission;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.cache.PersonRoleApi;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonToRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 人员角色组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/personRole", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PersonRoleApiImpl implements PersonRoleApi {

    private final Y9PersonToRoleService y9PersonToRoleService;

    /**
     * 根据人员id判断该人员是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param personId 人员id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToRoleService.hasPublicRole(personId, roleName));
    }

    /**
     * 判断人员是否拥有角色
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param personId 人员id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToRoleService.hasRole(personId, roleId));
    }

    /**
     * 根据人员id判断该人员是否拥有 roleName 这个角色
     *
     * @param tenantId 租户id
     * @param systemName 系统标识
     * @param properties 角色扩展属性
     * @param roleName 角色名称
     * @param personId 人员id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToRoleService.hasRole(personId, systemName, roleName, properties));
    }

    /**
     * 判断人员是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param customId 自定义id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> hasRoleByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToRoleService.hasRoleByCustomId(personId, customId));
    }

    /**
     * 获取拥有角色的所有人员（不包含禁用）集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersonsByRoleId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToRoleService.listPersonsByRoleId(roleId, Boolean.FALSE));
    }

    /**
     * 获取人员所拥有的角色集合
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是角色集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Role>> listRolesByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9PersonToRoleService.listRolesByPersonId(personId));
    }
}
