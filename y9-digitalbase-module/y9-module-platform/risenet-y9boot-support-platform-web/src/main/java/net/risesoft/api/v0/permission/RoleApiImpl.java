package net.risesoft.api.v0.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.v0.permission.RoleApi;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.permission.OrgBasesToRoles;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9EnumUtil;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 角色组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0RoleApiImpl")
@RequestMapping(value = "/services/rest/role", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class RoleApiImpl implements RoleApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrgBasesToRolesService y9OrgBasesToRolesService;
    private final Y9PersonService y9PersonService;
    private final Y9RoleService y9RoleService;

    /**
     * 新增人员到角色
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return boolean 是否增加成功
     * @since 9.6.0
     */
    @Override
    public boolean addPerson(@RequestParam("personId") @NotBlank String personId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9OrgBasesToRolesService.addOrgUnitsForRole(roleId, Collections.singletonList(personId), Boolean.TRUE);
        return true;
    }

    /**
     * 新增角色节点（带自定义标示customId）
     *
     * @param roleId 角色id
     * @param roleName 角色名称
     * @param parentId 父节点id
     * @param customId customId 自定义id
     * @param type 角色类型，node或者role
     * @return Role 角色对象
     * @since 9.6.0
     */
    @Override
    public Role createRole(@RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName,
        @RequestParam("parentId") String parentId, @RequestParam("customId") String customId,
        @RequestParam("type") String type) {
        Optional<Role> y9RoleOptional = y9RoleService.findByCustomIdAndParentId(customId, parentId);
        Role role;
        if (y9RoleOptional.isEmpty()) {
            role = new Role();
            role.setId(roleId);
            role.setCustomId(customId);
            role.setParentId(parentId);
            role.setType(Y9EnumUtil.valueOf(RoleTypeEnum.class, type));
        } else {
            role = y9RoleOptional.get();
        }
        role.setName(roleName);
        return y9RoleService.saveOrUpdate(role);
    }

    /**
     * 删除角色（同时删除该角色的授权关系）
     *
     * @param roleId 角色id
     * @return Boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    public Boolean deleteRole(@RequestParam("roleId") @NotBlank String roleId) {
        y9RoleService.delete(roleId);
        return true;
    }

    /**
     * 根据customId和parentId获取角色
     *
     * @param customId customId
     * @param parentId 角色的父节点id
     * @return Role 角色对象
     * @since 9.6.0
     */
    @Override
    public Role findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Role role = y9RoleService.findByCustomIdAndParentId(customId, parentId).orElse(null);
        return role;
    }

    /**
     * 根据id获取相应角色节点
     *
     * @param roleId 角色唯一标识
     * @return Role 角色对象
     * @since 9.6.0
     */
    @Override
    public Role getRole(@RequestParam("roleId") @NotBlank String roleId) {
        Role y9Role = y9RoleService.findById(roleId).orElse(null);
        return y9Role;
    }

    /**
     * 根据角色Id获取相应OrgUnits
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @param orgType 组织类型
     * @return {@code List<OrgUnit>} 机构对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> listOrgUnitsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("orgType") @NotBlank String orgType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<OrgBasesToRoles> orgBasesToRolesList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<OrgUnit> orgUnitList = new ArrayList<>();
        for (OrgBasesToRoles orgBasesToRoles : orgBasesToRolesList) {
            if (!Boolean.TRUE.equals(orgBasesToRoles.getNegative())) {
                OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgBasesToRoles.getOrgId());
                if (!orgType.equals(orgUnit.getOrgType().getEnName())) {
                    continue;
                }
                orgUnitList.add(orgUnit);
            }
        }
        Collections.sort(orgUnitList);
        return orgUnitList;
    }

    /**
     * 根据角色Id获取相应人员
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return {@code List<Person>} 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersonsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<OrgBasesToRoles> orgBasesToRolesList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<Person> persons = new ArrayList<>();
        for (OrgBasesToRoles orgBasesToRoles : orgBasesToRolesList) {
            if (!Boolean.TRUE.equals(orgBasesToRoles.getNegative())) {
                OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgBasesToRoles.getOrgId());
                if (!(OrgTypeEnum.PERSON.equals(orgUnit.getOrgType()))) {
                    continue;
                }
                persons.add((Person)orgUnit);
            }
        }
        return persons;
    }

    /**
     * 根据父节点Id获取相应子级角色节点
     *
     * @param roleId 角色唯一标识
     * @return {@code List<Role>} 角色对象集合
     * @since 9.6.0
     */
    @Override
    public List<Role> listRoleByParentId(@RequestParam("roleId") @NotBlank String roleId) {
        return y9RoleService.listByParentId(roleId);
    }

    /**
     * 删除角色中的人员
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    public boolean removePerson(@RequestParam("personId") @NotBlank String personId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        try {
            y9OrgBasesToRolesService.removeOrgBases(roleId, Collections.singletonList(personId));
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return false;
    }
}
