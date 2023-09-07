package net.risesoft.controller.role;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.role.vo.RoleMember;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.enums.Y9DepartmentPropCategoryEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 组织-角色关联管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/orgBasesToRoles", produces = "application/json")
@RequiredArgsConstructor
public class RoleMemberController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrgBasesToRolesService y9OrgBasesToRolesService;
    private final Y9DepartmentPropService y9DepartmentPropService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9RoleService y9RoleService;

    /**
     * 添加组织机构节点,对此角色的映射
     *
     * @param roleId 角色id
     * @param orgUnitIds 机构成员id数组
     * @param negative 是否为负角色关联
     * @return
     */
    @RiseLog(operationName = "添加组织机构节点,对此角色的映射", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addOrgUnits")
    public Y9Result<List<RoleMember>> addOrgUnits(@RequestParam String roleId, @RequestParam String[] orgUnitIds,
        @RequestParam Boolean negative) {
        String[] acssOrgUnitIds;
        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            acssOrgUnitIds = orgUnitIds;
        } else {
            List<Y9DepartmentProp> deptProps = y9DepartmentPropService.listByOrgBaseIdAndCategory(
                Y9LoginUserHolder.getPersonId(), Y9DepartmentPropCategoryEnum.ADMIN.getCategory());
            List<String> accessibleOrgUnitId = new ArrayList<>();
            for (String orgUnitId : orgUnitIds) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgUnitId);
                for (Y9DepartmentProp deptProp : deptProps) {
                    String dn = y9DepartmentService.getById(deptProp.getDeptId()).getDn();
                    if (y9OrgBase.getDn().contains(dn)) {
                        accessibleOrgUnitId.add(orgUnitId);
                        break;
                    }
                }
            }
            acssOrgUnitIds = accessibleOrgUnitId.toArray(new String[accessibleOrgUnitId.size()]);
        }

        List<Y9OrgBasesToRoles> roleMappingList =
            y9OrgBasesToRolesService.addOrgBases(roleId, acssOrgUnitIds, negative);
        List<RoleMember> memberList = new ArrayList<>();
        for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
            RoleMember roleMember = new RoleMember();
            roleMember.setId(roleMember.getId());
            Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
            roleMember.setOrgUnitId(roleMapping.getOrgId());
            roleMember.setUnitName(y9OrgBase.getName());
            roleMember.setUnitTypeName(OrgTypeEnum.ORG_TYPE_MAP.get(y9OrgBase.getOrgType()));
            roleMember.setUnitDn(y9OrgBase.getDn());
            roleMember.setNegative(roleMapping.getNegative() ? "是" : "否");
            memberList.add(roleMember);
        }

        return Y9Result.success(memberList, "获取数据成功");
    }

    /**
     * 根据角色id,返回角色关联的机构节点（机构，部门，用户组，岗位，人员）
     *
     * @param roleId 角色id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据角色id,返回角色关联的机构节点（机构，部门，用户组，岗位，人员）")
    @RequestMapping(value = "/listByRoleId")
    public Y9Result<List<RoleMember>> listByRoleId(@RequestParam String roleId) {
        List<Y9OrgBasesToRoles> roleMappingList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<RoleMember> memberList = new ArrayList<>();
        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
                if (y9OrgBase == null) {
                    continue;
                }
                RoleMember roleMember = new RoleMember();
                roleMember.setId(roleMember.getId());
                roleMember.setOrgUnitId(roleMapping.getOrgId());
                roleMember.setUnitName(y9OrgBase.getName());
                roleMember.setUnitTypeName(OrgTypeEnum.ORG_TYPE_MAP.get(y9OrgBase.getOrgType()));
                roleMember.setUnitDn(y9OrgBase.getDn());
                roleMember.setNegative(roleMapping.getNegative() ? "是" : "否");
                memberList.add(roleMember);
            }
        } else {
            List<Y9DepartmentProp> deptProps = y9DepartmentPropService.listByOrgBaseIdAndCategory(
                Y9LoginUserHolder.getPersonId(), Y9DepartmentPropCategoryEnum.ADMIN.getCategory());
            for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
                if (y9OrgBase == null) {
                    continue;
                }
                for (Y9DepartmentProp deptProp : deptProps) {
                    String dn = y9DepartmentService.getById(deptProp.getDeptId()).getDn();
                    if (y9OrgBase.getDn().contains(dn)) {
                        RoleMember roleMember = new RoleMember();
                        roleMember.setId(roleMember.getId());
                        roleMember.setOrgUnitId(roleMapping.getOrgId());
                        roleMember.setUnitName(y9OrgBase.getName());
                        roleMember.setUnitTypeName(OrgTypeEnum.ORG_TYPE_MAP.get(y9OrgBase.getOrgType()));
                        roleMember.setUnitDn(y9OrgBase.getDn());
                        roleMember.setNegative(roleMapping.getNegative() ? "是" : "否");
                        memberList.add(roleMember);
                        break;
                    }
                }
            }
        }
        return Y9Result.success(memberList, "获取数据成功");
    }

    /**
     * 对此角色中移除组织机构节点
     *
     * @param ids 织机构节点id数组
     * @return
     */
    @RiseLog(operationName = "对此角色中移除组织机构节点", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam Integer[] ids) {
        y9OrgBasesToRolesService.remove(ids);
        return Y9Result.successMsg("移除组织角色关联成功");
    }

    /**
     * 对组织机构节点,添加角色的映射
     *
     * @param orgUnitId 机构id
     * @param roleIds 角色id数组
     * @param negative 是否为负角色关联
     * @return
     */
    @RiseLog(operationName = "对组织机构节点,添加角色的映射", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveRoles")
    public Y9Result<List<Y9Role>> saveRoles(@RequestParam String orgUnitId, @RequestParam String[] roleIds,
        @RequestParam Boolean negative) {
        List<Y9OrgBasesToRoles> mappingList = y9OrgBasesToRolesService.saveRoles(orgUnitId, roleIds, negative);
        List<Y9Role> roleList = new ArrayList<>();
        for (Y9OrgBasesToRoles roleMapping : mappingList) {
            roleList.add(y9RoleService.findById(roleMapping.getRoleId()));
        }

        return Y9Result.success(roleList, "对组织机构节点,添加角色的映射成功");
    }

    /**
     * 根据名称和所属部门查找角色成员
     *
     * @param roleId 角色id
     * @param unitName 成员名称
     * @param unitDn 部门名称
     * @return
     */
    @RiseLog(operationName = "根据名称和所属部门查找角色成员")
    @RequestMapping(value = "/searchByUnitNameAndUnitDN")
    public Y9Result<List<RoleMember>> searchByUnitNameAndUnitDn(@RequestParam String roleId, String unitName,
        String unitDn) {
        List<Y9OrgBasesToRoles> roleMappingList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<RoleMember> memberList = new ArrayList<>();
        for (Y9OrgBasesToRoles roleMapping : roleMappingList) {
            Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(roleMapping.getOrgId());
            if (y9OrgBase == null) {
                continue;
            }
            boolean addEnable = false;
            if (StringUtils.isNotBlank(unitName) && StringUtils.isNotBlank(unitDn)) {
                if (y9OrgBase.getName().contains(unitName) && y9OrgBase.getDn().contains(unitDn)) {
                    addEnable = true;
                }

            } else if (StringUtils.isNotBlank(unitName)) {
                if (y9OrgBase.getName().contains(unitName)) {
                    addEnable = true;
                }
            } else if (StringUtils.isNotBlank(unitDn)) {
                if (y9OrgBase.getDn().contains(unitDn)) {
                    addEnable = true;
                }
            }

            if (StringUtils.isBlank(unitDn) && StringUtils.isBlank(unitName)) {
                addEnable = true;
            }

            if (addEnable) {
                RoleMember roleMember = new RoleMember();
                roleMember.setId(roleMapping.getId());
                roleMember.setOrgUnitId(roleMapping.getOrgId());
                roleMember.setUnitName(y9OrgBase.getName());
                roleMember.setUnitTypeName(OrgTypeEnum.ORG_TYPE_MAP.get(y9OrgBase.getOrgType()));
                roleMember.setUnitDn(y9OrgBase.getDn());
                roleMember.setNegative(roleMapping.getNegative() ? "是" : "否");
                memberList.add(roleMember);
            }
        }

        return Y9Result.success(memberList, "获取数据成功");
    }

}
