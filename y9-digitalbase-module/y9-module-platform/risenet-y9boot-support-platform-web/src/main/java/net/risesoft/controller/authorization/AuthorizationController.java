package net.risesoft.controller.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.LevelConsts;
import net.risesoft.controller.authorization.vo.AuthorizationVO;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;
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

    private AuthorizationVO getAuthorizationVOForOrgBase(Y9Authorization y9Authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        String orgId = y9Authorization.getPrincipalId();
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setOrgUnitId(y9OrgBase.getId());
        authorizationVO.setOrgUnitName(y9OrgBase.getName());
        authorizationVO.setOrgUnitNamePath(Y9OrgUtil.dnToNamePath(y9OrgBase.getDn()));
        authorizationVO.setOrgType(y9OrgBase.getOrgType().getName());
        authorizationVO.setResourceId(y9Authorization.getResourceId());
        authorizationVO.setResourceName(y9Authorization.getResourceName());
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer() == null ? "" : y9Authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(y9Authorization.getAuthority().getName());
        return authorizationVO;
    }

    private AuthorizationVO getAuthorizationVOForResourceBase(Y9Authorization y9Authorization,
        Y9ResourceBase y9ResourceBase) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setResourceId(y9ResourceBase.getId());
        authorizationVO.setResourceName(y9ResourceBase.getName());
        authorizationVO.setResourceNamePath(buildResourceNamePath(y9ResourceBase));
        authorizationVO.setResourceTypeStr(y9ResourceBase.getResourceType().getName());
        authorizationVO.setSystemCnName(y9SystemService.getById(y9ResourceBase.getSystemId()).getCnName());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(y9Authorization.getAuthority().getName());
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setUrl(y9ResourceBase.getUrl() == null ? "" : y9ResourceBase.getUrl());
        return authorizationVO;
    }

    private String buildResourceNamePath(Y9ResourceBase y9ResourceBase) {
        String guidPath = y9ResourceBase.getGuidPath();
        if (StringUtils.isNotEmpty(guidPath)) {
            List<String> resourceIdList = Arrays.asList(guidPath.split(LevelConsts.SEPARATOR));
            List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.findByIdIn(resourceIdList);
            return y9ResourceBaseList.stream().map(Y9ResourceBase::getName)
                .collect(Collectors.joining(LevelConsts.NAME_SEPARATOR));
        } else {
            return y9ResourceBase.getName();
        }
    }

    private AuthorizationVO getAuthorizationVOForRole(Y9Authorization y9Authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        Y9Role y9Role = y9RoleService.getById(y9Authorization.getPrincipalId());
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setRoleId(y9Authorization.getPrincipalId());
        authorizationVO.setRoleName(y9Role.getName());
        authorizationVO.setRoleNamePath(Y9OrgUtil.dnToNamePath(y9Role.getDn()));
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer() == null ? "" : y9Authorization.getAuthorizer());
        authorizationVO.setResourceId(y9Authorization.getResourceId());
        authorizationVO.setResourceName(y9Authorization.getResourceName());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(y9Authorization.getAuthority().getName());
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
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationService.listByPrincipalTypeNotAndResourceId(AuthorizationPrincipalTypeEnum.ROLE, resourceId);
        List<AuthorizationVO> authorizationVOList =
            y9AuthorizationList.stream().map(this::getAuthorizationVOForOrgBase).collect(Collectors.toList());
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
        Page<Y9Authorization> y9AuthorizationPage = y9AuthorizationService.pageByPrincipalId(roleId, pageQuery);
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        for (Y9Authorization y9Authorization : y9AuthorizationPage) {
            String resourceId = y9Authorization.getResourceId();
            Y9ResourceBase y9ResourceBase = compositeResourceService.findById(resourceId);
            if (y9ResourceBase == null) {
                continue;
            }
            authorizationVOList.add(getAuthorizationVOForResourceBase(y9Authorization, y9ResourceBase));
        }

        return Y9Page.success(pageQuery.getPage4Db(), y9AuthorizationPage.getTotalPages(),
            y9AuthorizationPage.getTotalElements(), authorizationVOList, "获取数据成功");
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
        List<Y9Authorization> y9AuthorizationList;

        if (StringUtils.isNotBlank(roleName)) {
            List<Y9Role> list = y9RoleService.listByName(roleName);
            List<String> roleIds = list.stream().map(Y9Role::getId).collect(Collectors.toList());
            if (!roleIds.isEmpty()) {
                y9AuthorizationList = y9AuthorizationService.listByRoleIds(roleIds, resourceId, authority);
            } else {
                y9AuthorizationList = new ArrayList<>();
            }
        } else {
            y9AuthorizationList = y9AuthorizationService
                .listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum.ROLE, resourceId);
        }
        for (Y9Authorization y9Authorization : y9AuthorizationList) {
            if (AuthorizationPrincipalTypeEnum.ROLE.equals(y9Authorization.getPrincipalType())) {
                authorizationVOList.add(getAuthorizationVOForRole(y9Authorization));
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
        List<Y9Authorization> y9AuthorizationList = y9AuthorizationService
            .listInheritByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum.ROLE, resourceId);

        for (Y9Authorization y9Authorization : y9AuthorizationList) {
            authorizationVOList.add(getAuthorizationVOForRole(y9Authorization));
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
        List<Y9Authorization> y9AuthorizationList =
            y9AuthorizationService.listInheritByPrincipalTypeIsOrgUnitAndResourceId(resourceId);

        for (Y9Authorization y9Authorization : y9AuthorizationList) {
            authorizationVOList.add(getAuthorizationVOForOrgBase(y9Authorization));
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
