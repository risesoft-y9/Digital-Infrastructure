package net.risesoft.api.permission;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Role;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.role.Y9Role;
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
@RestController
@RequestMapping(value = "/services/rest/role", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
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
     * @param systemName 系统标识
     * @param systemCnName 系统中文名称
     * @return Role 角色对象
     * @since 9.6.0
     */
    @Override
    public Role createRole(@RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName,
        @RequestParam("parentId") String parentId, @RequestParam("customId") String customId,
        @RequestParam("type") String type, @RequestParam("systemName") String systemName,
        @RequestParam("systemCnName") String systemCnName) {
        Optional<Y9Role> y9RoleOptional = y9RoleService.findByCustomIdAndParentId(customId, parentId);
        Y9Role roleNode;
        if (y9RoleOptional.isEmpty()) {
            roleNode = new Y9Role();
            roleNode.setId(roleId);
            roleNode.setCustomId(customId);
            roleNode.setParentId(parentId);
            roleNode.setType(type);
            roleNode.setSystemName(systemName);
            roleNode.setSystemCnName(systemCnName);
        } else {
            roleNode = y9RoleOptional.get();
        }
        roleNode.setName(roleName);
        roleNode = y9RoleService.saveOrUpdate(roleNode);
        return ModelConvertUtil.y9RoleToRole(roleNode);
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
        Y9Role roleNode = y9RoleService.findByCustomIdAndParentId(customId, parentId).orElse(null);
        return ModelConvertUtil.y9RoleToRole(roleNode);
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
        Y9Role y9Role = y9RoleService.findById(roleId).orElse(null);
        return ModelConvertUtil.y9RoleToRole(y9Role);
    }

    /**
     * 根据角色Id获取相应OrgUnits
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @param orgType 组织类型
     * @return List<OrgUnit> 机构对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> listOrgUnitsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("orgType") @NotBlank String orgType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBasesToRoles> roleMappingList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<Y9OrgBase> y9OrgBaseList = new ArrayList<>();
        for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
            if (!Boolean.TRUE.equals(roleMapping.getNegative())) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
                if (y9OrgBase == null || !orgType.equals(y9OrgBase.getOrgType())) {
                    continue;
                }
                y9OrgBaseList.add(y9OrgBase);
            }
        }
        Collections.sort(y9OrgBaseList);
        return ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList);
    }

    /**
     * 根据角色Id获取相应人员
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersonsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBasesToRoles> roleMappingList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<Person> persons = new ArrayList<>();
        for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
            if (!Boolean.TRUE.equals(roleMapping.getNegative())) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
                if (y9OrgBase == null || !("Person".equals(y9OrgBase.getOrgType()))) {
                    continue;
                }
                Y9Person y9Person = y9PersonService.getById(roleMapping.getOrgId());
                persons.add(Y9ModelConvertUtil.convert(y9Person, Person.class));
            }
        }
        return persons;
    }

    /**
     * 根据父节点Id获取相应子级角色节点
     *
     * @param roleId 角色唯一标识
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    @Override
    public List<Role> listRoleByParentId(@RequestParam("roleId") @NotBlank String roleId) {
        List<Y9Role> y9RoleList = y9RoleService.listByParentId(roleId);
        List<Role> roleList = new ArrayList<>();
        for (Y9Role y9Role : y9RoleList) {
            roleList.add(ModelConvertUtil.y9RoleToRole(y9Role));
        }
        return roleList;
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
