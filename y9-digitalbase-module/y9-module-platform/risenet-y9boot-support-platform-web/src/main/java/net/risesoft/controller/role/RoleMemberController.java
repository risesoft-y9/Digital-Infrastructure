package net.risesoft.controller.role;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.role.vo.RoleMemberVO;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * 组织-角色关联管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/orgBasesToRoles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager(ManagerLevelEnum.SECURITY_MANAGER)
public class RoleMemberController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrgBasesToRolesService y9OrgBasesToRolesService;
    private final Y9DepartmentService y9DepartmentService;

    /**
     * 对角色，添加组织节点的映射
     *
     * @param roleId 角色id
     * @param orgUnitIds 组织节点id数组
     * @param negative 是否为负角色关联
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "对角色，添加组织节点的映射", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addOrgUnits")
    public Y9Result<Object> addOrgUnitsForRole(@RequestParam @NotBlank String roleId,
        @RequestParam(value = "orgUnitIds") @NotEmpty List<String> orgUnitIds, @RequestParam Boolean negative) {

        List<String> accessibleOrgUnitIdList;

        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            accessibleOrgUnitIdList = orgUnitIds;
        } else {
            Y9Department managerDept = y9DepartmentService.getById(Y9LoginUserHolder.getDeptId());
            accessibleOrgUnitIdList = new ArrayList<>();
            for (String orgUnitId : orgUnitIds) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgUnitId);
                if (Y9OrgUtil.isSameOf(managerDept, y9OrgBase) || Y9OrgUtil.isAncestorOf(managerDept, y9OrgBase)) {
                    accessibleOrgUnitIdList.add(orgUnitId);
                }
            }
        }

        y9OrgBasesToRolesService.addOrgUnitsForRole(roleId, accessibleOrgUnitIdList, negative);

        return Y9Result.successMsg("对角色，添加组织节点的映射成功");
    }

    /**
     * 对组织节点，添加角色的映射
     *
     * @param orgUnitId 组织节点id
     * @param roleIds 角色id数组
     * @param negative 是否为负角色关联
     * @return {@code Y9Result<List<}{@link Y9Role}{@code >>}
     */
    @RiseLog(operationName = "对组织节点，添加角色的映射", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveRoles")
    public Y9Result<List<Y9Role>> addRolesForOrgUnit(@RequestParam @NotBlank String orgUnitId,
        @RequestParam(value = "roleIds") @NotEmpty List<String> roleIds, @RequestParam Boolean negative) {

        y9OrgBasesToRolesService.addRolesForOrgUnit(orgUnitId, roleIds, negative);

        return Y9Result.successMsg("对组织机构节点，添加角色的映射成功");
    }

    /**
     * 根据角色id，返回角色关联的机构节点（机构，部门，用户组，岗位，人员）
     *
     * @param roleId 角色id
     * @return {@code Y9Result<List<}{@link RoleMemberVO}{@code >>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据角色id，返回角色关联的机构节点（机构，部门，用户组，岗位，人员）")
    @RequestMapping(value = "/listByRoleId")
    public Y9Result<List<RoleMemberVO>> listByRoleId(@RequestParam @NotBlank String roleId) {
        List<Y9OrgBasesToRoles> y9OrgBasesToRolesList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<RoleMemberVO> memberList = new ArrayList<>();
        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(y9OrgBasesToRoles.getOrgId());
                memberList.add(RoleMemberVO.of(y9OrgBasesToRoles, y9OrgBase));
            }
        } else {
            for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
                Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(y9OrgBasesToRoles.getOrgId());
                Y9Department managerDept = y9DepartmentService.getById(Y9LoginUserHolder.getDeptId());
                if (Y9OrgUtil.isSameOf(managerDept, y9OrgBase) || Y9OrgUtil.isAncestorOf(managerDept, y9OrgBase)) {
                    memberList.add(RoleMemberVO.of(y9OrgBasesToRoles, y9OrgBase));
                }
            }
        }
        return Y9Result.success(memberList, "获取数据成功");
    }

    @RiseLog(operationName = "根据角色id，返回角色关联的机构节点 id 集合（用于添加角色成员的组织架构树的选择回显）")
    @GetMapping(value = "/listOrgUnitIdByRoleId")
    public Y9Result<List<String>> listOrgUnitIdByRoleId(@RequestParam @NotBlank String roleId,
        @RequestParam(required = false) Boolean negative) {
        List<String> orgUnitIdList = y9OrgBasesToRolesService.listOrgUnitIdByRoleId(roleId, negative);

        return Y9Result.success(orgUnitIdList, "获取数据成功");
    }

    /**
     * 角色组织节点关联移除
     *
     * @param ids 织机构节点id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "角色-组织节点关联移除", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam(value = "ids") @NotEmpty List<String> ids) {
        y9OrgBasesToRolesService.remove(ids);
        return Y9Result.successMsg("角色-组织节点关联移除成功");
    }

    /**
     * 根据名称和所属部门查找角色成员
     *
     * @param roleId 角色id
     * @param unitName 成员名称
     * @return {@code Y9Result<List<}{@link RoleMemberVO}{@code >>}
     */
    @RiseLog(operationName = "根据名称和所属部门查找角色成员")
    @RequestMapping(value = "/searchByUnitName")
    public Y9Page<RoleMemberVO> searchByUnitName(@RequestParam @NotBlank String roleId, String unitName,
        Y9PageQuery pageQuery) {
        Page<Y9OrgBasesToRoles> y9OrgBasesToRolesList = y9OrgBasesToRolesService.page(pageQuery, roleId, unitName);

        List<RoleMemberVO> memberList = y9OrgBasesToRolesList.getContent().stream()
            .map(o -> RoleMemberVO.of(o, compositeOrgBaseService.getOrgUnit(o.getOrgId())))
            .collect(Collectors.toList());

        return Y9Page.success(pageQuery.getPage(), y9OrgBasesToRolesList.getTotalPages(),
            y9OrgBasesToRolesList.getTotalElements(), memberList);
    }

}
