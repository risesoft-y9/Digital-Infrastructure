package net.risesoft.controller.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.permission.OrgBasesToRoles;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.relation.Y9OrgBasesToRolesService;
import net.risesoft.vo.role.RoleMemberVO;
import net.risesoft.y9.Y9LoginUserHolder;

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

    private final Y9ManagerService y9ManagerService;

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
            accessibleOrgUnitIdList =
                y9ManagerService.filterManagableOrgUnitList(Y9LoginUserHolder.getDeptId(), orgUnitIds)
                    .stream()
                    .map(OrgUnit::getId)
                    .collect(Collectors.toList());
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
     * @return {@code Y9Result<Object> }
     */
    @RiseLog(operationName = "对组织节点，添加角色的映射", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveRoles")
    public Y9Result<Object> addRolesForOrgUnit(@RequestParam @NotBlank String orgUnitId,
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
        List<OrgBasesToRoles> orgBasesToRolesList = y9OrgBasesToRolesService.listByRoleId(roleId);
        List<RoleMemberVO> roleMemberVOList = new ArrayList<>();
        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            for (OrgBasesToRoles orgBasesToRoles : orgBasesToRolesList) {
                OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgBasesToRoles.getOrgId());
                roleMemberVOList.add(RoleMemberVO.of(orgBasesToRoles, orgUnit));
            }
        } else {
            List<String> orgUnitIdList =
                orgBasesToRolesList.stream().map(OrgBasesToRoles::getOrgId).collect(Collectors.toList());
            List<OrgUnit> orgUnitList =
                y9ManagerService.filterManagableOrgUnitList(Y9LoginUserHolder.getPersonId(), orgUnitIdList);
            for (OrgUnit orgUnit : orgUnitList) {
                orgBasesToRolesList.stream()
                    .filter(orgBasesToRoles -> Objects.equals(orgBasesToRoles.getOrgId(), orgUnit.getId()))
                    .findFirst()
                    .ifPresent(orgBasesToRoles -> {
                        roleMemberVOList.add(RoleMemberVO.of(orgBasesToRoles, orgUnit));
                    });
            }
        }
        return Y9Result.success(roleMemberVOList, "获取数据成功");
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
        Y9Page<OrgBasesToRoles> orgBasesToRolesY9Page = y9OrgBasesToRolesService.page(pageQuery, roleId, unitName);

        List<RoleMemberVO> memberList = orgBasesToRolesY9Page.getRows()
            .stream()
            .map(o -> RoleMemberVO.of(o, compositeOrgBaseService.getOrgUnit(o.getOrgId())))
            .collect(Collectors.toList());

        return Y9Page.success(pageQuery.getPage(), orgBasesToRolesY9Page.getTotalPages(),
            orgBasesToRolesY9Page.getTotal(), memberList);
    }

}
