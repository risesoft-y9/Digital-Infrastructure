package net.risesoft.controller.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.LevelConsts;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.permission.Authorization;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.Y9AuthorizationService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.vo.permission.AuthorizationVO;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 资源权限管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 * @since 9.6.0
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/authorization", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@IsAnyManager(ManagerLevelEnum.SECURITY_MANAGER)
public class AuthorizationController {

    private final Y9AuthorizationService y9AuthorizationService;
    private final CompositeResourceService compositeResourceService;
    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9RoleService y9RoleService;
    private final Y9SystemService y9SystemService;

    private AuthorizationVO getAuthorizationVOForOrgBase(Authorization authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        String orgId = authorization.getPrincipalId();
        OrgUnit orgUnit = compositeOrgBaseService.getOrgUnit(orgId);
        authorizationVO.setId(authorization.getId());
        authorizationVO.setOrgUnitId(orgUnit.getId());
        authorizationVO.setOrgUnitName(orgUnit.getName());
        authorizationVO.setOrgUnitNamePath(Y9OrgUtil.dnToNamePath(orgUnit.getDn()));
        authorizationVO.setOrgType(orgUnit.getOrgType().getName());
        authorizationVO.setResourceId(authorization.getResourceId());
        authorizationVO.setResourceName(authorization.getResourceName());
        authorizationVO.setAuthorizer(authorization.getAuthorizer() == null ? "" : authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(authorization.getCreateTime());
        authorizationVO.setAuthority(authorization.getAuthority());
        authorizationVO.setAuthorityStr(authorization.getAuthority().getName());
        return authorizationVO;
    }

    private AuthorizationVO getAuthorizationVOForResource(Authorization authorization, Resource resource) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        authorizationVO.setId(authorization.getId());
        authorizationVO.setResourceId(resource.getId());
        authorizationVO.setResourceName(resource.getName());
        authorizationVO.setResourceNamePath(buildResourceNamePath(resource));
        authorizationVO.setResourceTypeStr(resource.getResourceType().getName());
        authorizationVO.setSystemCnName(y9SystemService.getById(resource.getSystemId()).getCnName());
        authorizationVO.setAuthority(authorization.getAuthority());
        authorizationVO.setAuthorityStr(authorization.getAuthority().getName());
        authorizationVO.setAuthorizer(authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(authorization.getCreateTime());
        authorizationVO.setUrl(resource.getUrl() == null ? "" : resource.getUrl());
        return authorizationVO;
    }

    private String buildResourceNamePath(Resource resource) {
        String guidPath = resource.getGuidPath();
        if (StringUtils.isNotEmpty(guidPath)) {
            List<String> resourceIdList = Arrays.asList(guidPath.split(LevelConsts.SEPARATOR));
            List<Resource> resourceList = compositeResourceService.findByIdIn(resourceIdList);
            return resourceList.stream().map(Resource::getName).collect(Collectors.joining(LevelConsts.NAME_SEPARATOR));
        } else {
            return resource.getName();
        }
    }

    private AuthorizationVO getAuthorizationVOForRole(Authorization authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        Role role = y9RoleService.getById(authorization.getPrincipalId());
        authorizationVO.setId(authorization.getId());
        authorizationVO.setRoleId(authorization.getPrincipalId());
        authorizationVO.setRoleName(role.getName());
        authorizationVO.setRoleNamePath(Y9OrgUtil.dnToNamePath(role.getDn()));
        authorizationVO.setAuthorizer(authorization.getAuthorizer() == null ? "" : authorization.getAuthorizer());
        authorizationVO.setResourceId(authorization.getResourceId());
        authorizationVO.setResourceName(authorization.getResourceName());
        authorizationVO.setAuthorizeTime(authorization.getCreateTime());
        authorizationVO.setAuthority(authorization.getAuthority());
        authorizationVO.setAuthorityStr(authorization.getAuthority().getName());
        return authorizationVO;
    }

    /**
     * 根据资源id获取关联的角色列表
     *
     * @param resourceId 资源id
     * @return {@code Y9Result<List<}{@link AuthorizationVO}{@code >>}
     */
    @RiseLog(operationName = "根据资源id获取关联的组织列表 ")
    @RequestMapping(value = "/listRelateOrgList")
    public Y9Result<List<AuthorizationVO>> listRelateOrgList(@NotBlank @RequestParam("resourceId") String resourceId) {
        List<Authorization> authorizationList =
            y9AuthorizationService.listByResourceIdAndPrincipalTypeNot(resourceId, AuthorizationPrincipalTypeEnum.ROLE);
        List<AuthorizationVO> authorizationVOList =
            authorizationList.stream().map(this::getAuthorizationVOForOrgBase).collect(Collectors.toList());
        return Y9Result.success(authorizationVOList, "获取数据成功！");
    }

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @param pageQuery 分页查询
     * @return {@code Y9Page<AuthorizationVO>}
     */
    @RiseLog(operationName = "根据角色ID获取权限列表 ")
    @RequestMapping(value = "/listRelateResource")
    public Y9Page<AuthorizationVO> listRelateResource(@RequestParam("roleId") @NotBlank String roleId,
        Y9PageQuery pageQuery) {
        Y9Page<Authorization> authorizationY9Page = y9AuthorizationService.pageByPrincipalId(roleId, pageQuery);
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        for (Authorization authorization : authorizationY9Page.getRows()) {
            String resourceId = authorization.getResourceId();
            Resource resource = compositeResourceService.findById(resourceId);
            if (resource == null) {
                continue;
            }
            authorizationVOList.add(getAuthorizationVOForResource(authorization, resource));
        }
        return Y9Page.success(pageQuery.getPage(), authorizationY9Page.getTotalPages(), authorizationY9Page.getTotal(),
            authorizationVOList, "获取数据成功");
    }

    /**
     * 根据角色 id，返回授权的资源 id 集合（用于添加资源授权的资源树的选择回显）
     *
     * @param roleId 角色 id
     * @param authority 权限类型
     * @return {@code Y9Result<List<String>> }
     */
    @RiseLog(operationName = "根据角色 id，返回授权的资源 id 集合（用于添加资源授权的资源树的选择回显）")
    @GetMapping(value = "/listResourceIdByRoleId")
    public Y9Result<List<String>> listResourceIdByRoleId(@RequestParam @NotBlank String roleId,
        @RequestParam AuthorityEnum authority) {
        List<String> orgUnitIdList = y9AuthorizationService.listResourceIdByPrincipleId(roleId, authority);
        return Y9Result.success(orgUnitIdList, "获取数据成功");
    }

    /**
     * 根据资源 id，返回授权主体的 id 集合（用于添加资源授权的角色或组织架构树的选择回显）
     *
     * @param resourceId 资源 id
     * @param authority 权限类型
     * @return {@code Y9Result<List<String>> }
     */
    @RiseLog(operationName = "根据资源 id，返回授权主体的 id 集合（用于添加资源授权的角色或组织架构树的选择回显）")
    @GetMapping(value = "/listPrincipalIdByResourceId")
    public Y9Result<List<String>> listPrincipalIdByResourceId(@RequestParam @NotBlank String resourceId,
        @RequestParam AuthorityEnum authority) {
        List<String> orgUnitIdList = y9AuthorizationService.listPrincipalIdByResourceId(resourceId, authority);
        return Y9Result.success(orgUnitIdList, "获取数据成功");
    }

    /**
     * 根据资源id获取关联的角色列表
     *
     * @param resourceId 资源id
     * @param roleName 角色名
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code Y9Result<List<AuthorizationVO>>}
     */
    @RiseLog(operationName = "根据资源id获取关联的角色列表 ")
    @RequestMapping(value = "/listRelateRole")
    public Y9Result<List<AuthorizationVO>> listRelateRole(@RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam(required = false) String roleName, @RequestParam("authority") AuthorityEnum authority) {
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        List<Authorization> authorizationList;
        if (StringUtils.isNotBlank(roleName)) {
            List<Role> list = y9RoleService.listByName(roleName);
            List<String> roleIds = list.stream().map(Role::getId).collect(Collectors.toList());
            if (!roleIds.isEmpty()) {
                authorizationList = y9AuthorizationService.listByRoleIds(roleIds, resourceId, authority);
            } else {
                authorizationList = new ArrayList<>();
            }
        } else {
            authorizationList = y9AuthorizationService
                .listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum.ROLE, resourceId);
        }
        for (Authorization authorization : authorizationList) {
            if (AuthorizationPrincipalTypeEnum.ROLE.equals(authorization.getPrincipalType())) {
                authorizationVOList.add(getAuthorizationVOForRole(authorization));
            }
        }
        return Y9Result.success(authorizationVOList, "获取数据成功！");
    }

    /**
     * 根据资源id获取继承的角色授权
     *
     * @param resourceId 资源id
     * @return {@code Y9Result<List<AuthorizationVO>>}
     */
    @RiseLog(operationName = "根据资源id获取继承的角色授权")
    @RequestMapping(value = "/listInheritRole")
    public Y9Result<List<AuthorizationVO>> listInheritRole(@RequestParam("resourceId") @NotBlank String resourceId) {
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        List<Authorization> authorizationList = y9AuthorizationService
            .listInheritByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum.ROLE, resourceId);
        for (Authorization authorization : authorizationList) {
            authorizationVOList.add(getAuthorizationVOForRole(authorization));
        }
        return Y9Result.success(authorizationVOList, "获取数据成功！");
    }

    /**
     * 根据资源id获取继承的组织节点授权
     *
     * @param resourceId 资源id
     * @return {@code Y9Result<List<AuthorizationVO>>}
     */
    @RiseLog(operationName = "根据资源id获取继承的组织节点授权")
    @RequestMapping(value = "/listInheritOrg")
    public Y9Result<List<AuthorizationVO>> listInheritOrg(@RequestParam("resourceId") @NotBlank String resourceId) {
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        List<Authorization> authorizationList =
            y9AuthorizationService.listInheritByPrincipalTypeIsOrgUnitAndResourceId(resourceId);
        for (Authorization authorization : authorizationList) {
            authorizationVOList.add(getAuthorizationVOForOrgBase(authorization));
        }
        return Y9Result.success(authorizationVOList, "获取数据成功！");
    }

    /**
     * 移除角色授权许可记录
     *
     * @param ids 角色权限许可id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "移除角色授权许可记录", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam("ids") @NotEmpty String[] ids) {
        y9AuthorizationService.delete(ids);
        return Y9Result.successMsg("移除角色授权许可记录成功");
    }

    /**
     * 保存关联资源权限许可对象
     *
     * @param resourceIds 资源id数组
     * @param authority 权限类型
     * @param principalId 授权主体id
     * @param principalType 授权主体类型
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存管理资源权限许可对象 ", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(AuthorityEnum authority, @NotBlank String principalId,
        AuthorizationPrincipalTypeEnum principalType, @RequestParam("resourceIds") @NotEmpty String[] resourceIds) {
        y9AuthorizationService.save(authority, principalId, principalType, resourceIds);
        return Y9Result.successMsg("授权成功！");
    }

    /**
     * 保存资源授权管理关联组织信息
     *
     * @param authority 权限类型
     * @param resourceId 资源id
     * @param orgIds 组织节点id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存资源授权管理关联组织信息", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdateOrg")
    public Y9Result<String> saveOrUpdateOrg(AuthorityEnum authority, @NotBlank String resourceId,
        @RequestParam("orgIds") @NotEmpty String[] orgIds) {
        y9AuthorizationService.saveByOrg(authority, resourceId, orgIds);
        return Y9Result.successMsg("授权成功！");
    }

    /**
     * 保存关联角色权限许可对象
     *
     * @param authority 权限类型
     * @param resourceId 资源id
     * @param roleIds 角色id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存关联角色权限许可对象 ", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdateRole")
    public Y9Result<String> saveOrUpdateRole(AuthorityEnum authority, @NotBlank String resourceId,
        @RequestParam("roleIds") @NotEmpty String[] roleIds) {
        y9AuthorizationService.saveByRoles(authority, resourceId, roleIds);
        return Y9Result.successMsg("授权成功！");
    }

}
