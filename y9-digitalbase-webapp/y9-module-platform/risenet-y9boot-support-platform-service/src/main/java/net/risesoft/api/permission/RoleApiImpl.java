package net.risesoft.api.permission;

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

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Result;
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
@RequestMapping(value = "/services/rest/v1/role", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RoleApiImpl implements RoleApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrgBasesToRolesService y9OrgBasesToRolesService;
    private final Y9PersonService y9PersonService;
    private final Y9RoleService y9RoleService;

    /**
     * 添加人员到角色
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> addPerson(@RequestParam("personId") @NotBlank String personId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9OrgBasesToRolesService.addOrgUnitsForRole(roleId, Collections.singletonList(personId), Boolean.TRUE);
        return Y9Result.success();
    }

    /**
     * 新增角色节点
     *
     * @param roleId 角色id
     * @param roleName 角色名称
     * @param parentId 父节点id
     * @param customId 自定义id
     * @param type 角色类型，node或者role
     * @return {@code Y9Result<Role>} 通用请求返回对象 - data 是保存的角色对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Role> createRole(@RequestParam("roleId") String roleId, @RequestParam("roleName") String roleName,
        @RequestParam("parentId") String parentId, @RequestParam("customId") String customId,
        @RequestParam("type") RoleTypeEnum type) {
        Optional<Y9Role> y9RoleOptional = y9RoleService.findByCustomIdAndParentId(customId, parentId);
        Y9Role roleNode;
        if (y9RoleOptional.isEmpty()) {
            roleNode = new Y9Role();
            roleNode.setId(roleId);
            roleNode.setCustomId(customId);
            roleNode.setParentId(parentId);
            roleNode.setType(type);
        } else {
            roleNode = y9RoleOptional.get();
        }
        roleNode.setName(roleName);
        roleNode = y9RoleService.saveOrUpdate(roleNode);
        return Y9Result.success(ModelConvertUtil.y9RoleToRole(roleNode));
    }

    /**
     * 删除角色
     *
     * @param roleId 角色id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> deleteRole(@RequestParam("roleId") @NotBlank String roleId) {
        y9RoleService.delete(roleId);
        return Y9Result.success();
    }

    /**
     * 根据customId和parentId查找角色对象
     *
     * @param customId customId
     * @param parentId 角色的父节点id
     * @return {@code Y9Result<Role>} 通用请求返回对象 - data 是角色对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Role> findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9Role roleNode = y9RoleService.findByCustomIdAndParentId(customId, parentId).orElse(null);
        return Y9Result.success(ModelConvertUtil.y9RoleToRole(roleNode));
    }

    /**
     * 根据id获取相应角色节点
     *
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<Role>} 通用请求返回对象 - data 是角色对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Role> getRole(@RequestParam("roleId") @NotBlank String roleId) {
        Y9Role y9Role = y9RoleService.findById(roleId).orElse(null);
        return Y9Result.success(ModelConvertUtil.y9RoleToRole(y9Role));
    }

    /**
     * 根据角色Id获取关联的组织节点集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @param orgType 数据类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> listOrgUnitsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("orgType") OrgTypeEnum orgType) {
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
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList));
    }

    /**
     * 根据角色Id获取直接关联的人员对象集合
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersonsById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("roleId") @NotBlank String roleId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBasesToRoles> roleMappingList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<Person> persons = new ArrayList<>();
        for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
            if (!Boolean.TRUE.equals(roleMapping.getNegative())) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
                if (y9OrgBase == null || !(OrgTypeEnum.PERSON.equals(y9OrgBase.getOrgType()))) {
                    continue;
                }
                Y9Person y9Person = y9PersonService.getById(roleMapping.getOrgId());
                persons.add(Y9ModelConvertUtil.convert(y9Person, Person.class));
            }
        }
        return Y9Result.success(persons);
    }

    /**
     * 根据父节点Id获取相应子级角色节点
     *
     * @param roleId 角色唯一标识
     * @return {@code Y9Result<List<Role>>} 通用请求返回对象 - data 是角色对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Role>> listRoleByParentId(@RequestParam("roleId") @NotBlank String roleId) {
        List<Y9Role> y9RoleList = y9RoleService.listByParentId(roleId);
        List<Role> roleList = new ArrayList<>();
        for (Y9Role y9Role : y9RoleList) {
            roleList.add(ModelConvertUtil.y9RoleToRole(y9Role));
        }
        return Y9Result.success(roleList);
    }

    /**
     * 删除角色关联的人员
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> removePerson(@RequestParam("personId") @NotBlank String personId,
        @RequestParam("roleId") @NotBlank String roleId, @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        y9OrgBasesToRolesService.removeOrgBases(roleId, Collections.singletonList(personId));
        return Y9Result.success();
    }
}
