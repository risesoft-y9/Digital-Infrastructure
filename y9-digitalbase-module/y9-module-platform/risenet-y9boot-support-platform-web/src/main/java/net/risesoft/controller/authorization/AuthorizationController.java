package net.risesoft.controller.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

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
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.CompositeResourceService;
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

    private AuthorizationVO getAuthorizationVOForOrgBase(Y9Authorization y9Authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        String orgId = y9Authorization.getPrincipalId();
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
        String dn = y9OrgBase.getDn();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setOrgId(y9OrgBase.getId());
        authorizationVO.setOrgName(dn.replace("cn=", "").replace(",ou=", " >> ").replace(",o=", " >> "));
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
        Y9ResourceBase acResource) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setResourceId(acResource.getId());
        authorizationVO.setResourceName(acResource.getName());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(y9Authorization.getAuthority().getName());
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer() == null ? "" : y9Authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setUrl(acResource.getUrl() == null ? "" : acResource.getUrl());
        return authorizationVO;
    }

    private AuthorizationVO getAuthorizationVOForRole(Y9Authorization y9Authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        String roleId = y9Authorization.getPrincipalId();
        String dn = y9RoleService.getById(roleId).getDn();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setRoleId(y9Authorization.getPrincipalId());
        authorizationVO.setRoleName(dn.replace(",cn=", " >> ").replace("cn=", ""));
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
     * @param page 页数
     * @param rows 条数
     * @return {@code Y9Page<AuthorizationVO>}
     */
    @RiseLog(operationName = "根据角色ID获取权限列表 ")
    @RequestMapping(value = "/listRelateResource")
    public Y9Page<AuthorizationVO> listRelateResource(@RequestParam("roleId") @NotBlank String roleId,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) {
        Page<Y9Authorization> y9AuthorizationPage = y9AuthorizationService.pageByPrincipalId(roleId, rows, page);
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        for (Y9Authorization y9Authorization : y9AuthorizationPage) {
            String resourceId = y9Authorization.getResourceId();
            Y9ResourceBase acResource = compositeResourceService.findById(resourceId);
            if (acResource == null) {
                continue;
            }
            authorizationVOList.add(getAuthorizationVOForResourceBase(y9Authorization, acResource));
        }

        return Y9Page.success(page, y9AuthorizationPage.getTotalPages(), y9AuthorizationPage.getTotalElements(),
            authorizationVOList, "获取数据成功");
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
            .listByPrincipalTypeAndResourceInherit(AuthorizationPrincipalTypeEnum.ROLE, resourceId);

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
        List<Y9Authorization> y9AuthorizationList = y9AuthorizationService
            .listByPrincipalTypeNotAndResourceInherit(AuthorizationPrincipalTypeEnum.ROLE, resourceId);

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
