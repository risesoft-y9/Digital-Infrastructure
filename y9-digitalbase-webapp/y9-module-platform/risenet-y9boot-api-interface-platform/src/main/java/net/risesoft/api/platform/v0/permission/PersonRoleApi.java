package net.risesoft.api.platform.v0.permission;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Role;

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
@Deprecated
public interface PersonRoleApi {

    /**
     * 根据人员id获取该人员拥有的角色数
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return long 角色数
     * @since 9.6.0
     */
    @GetMapping("/countByPersonId")
    long countByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据人员id判断该人员是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param personId 人员id
     * @return boolean
     * @since 9.6.0
     */
    @GetMapping("/hasPublicRole")
    Boolean hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId);

    /**
     * 判断人员是否拥有角色
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param personId 人员id
     * @return {@link Boolean}
     * @since 9.6.0
     */
    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("personId") @NotBlank String personId);

    /**
     * 判断人员是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param customId 自定义id
     * @return {@link Boolean}
     * @since 9.6.0
     */
    @GetMapping("/hasRole2")
    Boolean hasRoleByCustomId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId, @RequestParam("customId") @NotBlank String customId);

    /**
     * 根据人员id判断该人员是否拥有 roleName 这个角色
     *
     * @param tenantId 租户id
     * @param systemName 系统标识
     * @param properties 角色扩展属性
     * @param roleName 角色名称
     * @param personId 人员id
     * @return Boolean 是否拥有
     * @since 9.6.0
     */
    @GetMapping("/hasRole3")
    Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据角色Id获取角色下所有人员
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsByRoleId")
    List<Person> listPersonsByRoleId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据人员id获取所有关联的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRolesByPersonId")
    List<Role> listRolesByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);
}
