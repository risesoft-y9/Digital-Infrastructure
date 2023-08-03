package net.risesoft.api.permission;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Role;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface RoleApi {

    /**
     * 新增人员到角色
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return boolean 是否增加成功
     * @since 9.6.0
     */
    @PostMapping("/addPerson")
    boolean addPerson(@RequestParam("personId") @NotBlank String personId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 新增角色节点
     *
     * @param roleId 角色id
     * @param roleName 角色名称
     * @param parentId 父节点id
     * @param customId customId对应工作流的processDefineKey
     * @param type 角色类型，systemNode、tenantNode、node或者role
     * @param systemName 系统标识
     * @param systemCnName 系统中文名称
     * @return Role 角色对象
     * @since 9.6.0
     */
    @PostMapping("/createRoleNodeAddCustomId")
    Role createRoleNodeAddCustomId(@RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName,
        @RequestParam("parentId") String parentId, @RequestParam("customId") String customId,
        @RequestParam("type") String type, @RequestParam("systemName") String systemName,
        @RequestParam("systemCnName") String systemCnName);

    /**
     * 删除权限节点
     *
     * @param roleId 角色id
     * @return Boolean 是否删除成功
     * @since 9.6.0
     */

    @PostMapping("/deleteRole")
    Boolean deleteRole(@RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据customId(对应taskdefineKey或者processDefineKey)和parentId
     *
     * @param customId customId
     * @param parentId 角色的父节点id
     * @return Role 角色对象
     * @since 9.6.0
     */
    @GetMapping("/findByCustomIdAndParentId")
    Role findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
        @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 根据id获取相应角色节点
     *
     * @param roleId 角色唯一标识
     * @return Role 角色对象
     * @since 9.6.0
     */
    @GetMapping("/getRole")
    Role getRole(@RequestParam("roleId") @NotBlank String roleId);

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
    boolean hasPublicRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId);

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

    @GetMapping("/hasRole")
    Boolean hasRole(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam(value = "properties", required = false) String properties,
        @RequestParam("roleName") @NotBlank String roleName, @RequestParam("personId") @NotBlank String personId);

    /**
     * 判断orgUnitId是否有角色roleId
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param orgUnitId 组织架构节点id
     * @return Boolean 是否有
     * @since 9.6.0
     */

    @GetMapping("/hasRoleByTenantIdAndRoleIdAndOrgUnitId")
    Boolean hasRoleByTenantIdAndRoleIdAndOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据角色Id获取角色下所有人员（递归）
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllPersonsById")
    List<Person> listAllPersonsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据角色Id获取相应OrgUnits
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @param orgType 数据类型
     * @return List&lt;OrgUnit&gt; 机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/listOrgUnitsById")
    List<OrgUnit> listOrgUnitsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("orgType") @NotBlank String orgType);

    /**
     * 根据角色Id获取相应人员
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsById")
    List<Person> listPersonsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId);

    /**
     * 根据人员id获取所有关联的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRelateRoleByPersonId")
    List<Role> listRelateRoleByPersonId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("personId") @NotBlank String personId);

    /**
     * 根据orgUnitId获取角色节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织架构节点id
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRoleByOrgUnitId")
    List<Role> listRoleByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据父节点Id获取相应子级角色节点
     *
     * @param roleId 角色唯一标识
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRoleByParentId")
    List<Role> listRoleByParentId(@RequestParam("roleId") @NotBlank String roleId);

    /**
     * 删除角色中的人员
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */

    @PostMapping("/removePerson")
    boolean removePerson(@RequestParam("personId") @NotBlank String personId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId);
}
