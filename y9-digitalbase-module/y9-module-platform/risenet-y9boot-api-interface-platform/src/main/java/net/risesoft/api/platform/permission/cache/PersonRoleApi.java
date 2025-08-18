package net.risesoft.api.platform.permission.cache;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Result;

/**
 * 人员角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface PersonRoleApi {

    /**
     * 根据人员id判断该人员是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param personId 人员id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @GetMapping("/hasPublicRole")
    Y9Result<Boolean> hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId);

    /**
     * 判断人员是否拥有角色
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param personId 人员id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @GetMapping("/hasRole")
    Y9Result<Boolean> hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("personId") @NotBlank String personId);

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
    @GetMapping("/hasRole3")
    Y9Result<Boolean> hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId);

    /**
     * 判断人员是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param customId 自定义id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断是否拥有角色
     * @since 9.6.0
     */
    @GetMapping("/hasRole2")
    Y9Result<Boolean> hasRoleByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId);

    /**
     * 获取拥有角色的所有人员（不包含禁用）集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsByRoleId")
    Y9Result<List<Person>> listPersonsByRoleId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId);

    /**
     * 获取人员所拥有的角色集合
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是角色集合
     * @since 9.6.0
     */
    @GetMapping("/listRolesByPersonId")
    Y9Result<List<Role>> listRolesByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);
}
