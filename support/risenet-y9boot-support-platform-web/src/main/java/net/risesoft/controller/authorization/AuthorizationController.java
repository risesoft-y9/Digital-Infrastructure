package net.risesoft.controller.authorization;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
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
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.enums.AuthorizationPrincipalTypeEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
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
@RequestMapping(value = "/api/rest/authorization", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
public class AuthorizationController {

    private final Y9AuthorizationService y9AuthorizationService;
    private final CompositeResourceService compositeResourceService;
    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9RoleService y9RoleService;

    /**
     * 获取角色权限许可对象
     *
     * @param roleId 角色id
     * @param resourceId 资源id
     * @return
     */
    @RiseLog(operationName = "获取角色权限许可对象")
    @RequestMapping(value = "/get")
    public Y9Result<Y9Authorization> get(@NotBlank @RequestParam("roleId") String roleId,
        @NotBlank @RequestParam("resourceId") String resourceId) {
        Y9Authorization y9Authorization =
            y9AuthorizationService.listByPrincipalIdAndResourceId(roleId, resourceId).get(0);
        return Y9Result.success(y9Authorization, "获取角色权限许可对象成功！");
    }

    private AuthorizationVO getAuthorizationVOForOrgBase(Y9Authorization y9Authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        String orgId = y9Authorization.getPrincipalId();
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnit(orgId);
        String dn = y9OrgBase.getDn();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setOrgId(y9OrgBase.getId());
        authorizationVO.setOrgName(dn.replace("cn=", "").replace(",ou=", " >> ").replace(",o=", " >> "));
        authorizationVO.setOrgType(OrgTypeEnum.ORG_TYPE_MAP.get(y9OrgBase.getOrgType()));
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer() == null ? "" : y9Authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(AuthorityEnum.getByValue(y9Authorization.getAuthority()).getName());
        return authorizationVO;
    }

    private AuthorizationVO getAuthorizationVOForResourceBase(Y9Authorization y9Authorization,
        Y9ResourceBase acResource) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setResourceId(acResource.getId());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(AuthorityEnum.getByValue(y9Authorization.getAuthority()).getName());
        authorizationVO.setResourceName(acResource.getName());
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer() == null ? "" : y9Authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setUrl(acResource.getUrl() == null ? "" : acResource.getUrl());
        return authorizationVO;
    }

    private AuthorizationVO getAuthorizationVOForRole(Y9Authorization y9Authorization) {
        AuthorizationVO authorizationVO = new AuthorizationVO();
        String roleId = y9Authorization.getPrincipalId();
        String dn = y9RoleService.findById(roleId).getDn();
        authorizationVO.setId(y9Authorization.getId());
        authorizationVO.setRoleId(y9Authorization.getPrincipalId());
        authorizationVO.setRoleName(dn.replace(",cn=", " >> ").replace("cn=", ""));
        authorizationVO.setAuthorizer(y9Authorization.getAuthorizer() == null ? "" : y9Authorization.getAuthorizer());
        authorizationVO.setAuthorizeTime(y9Authorization.getCreateTime());
        authorizationVO.setAuthority(y9Authorization.getAuthority());
        authorizationVO.setAuthorityStr(AuthorityEnum.getByValue(y9Authorization.getAuthority()).getName());
        return authorizationVO;
    }

    /**
     * 根据资源id获取关联的角色列表
     *
     * @param resourceId 资源id
     * @return
     */
    @RiseLog(operationName = "根据资源id获取关联的组织列表 ")
    @RequestMapping(value = "/listRelateOrgList")
    public Y9Result<List<AuthorizationVO>> listRelateOrgList(@NotBlank @RequestParam("resourceId") String resourceId) {
        List<Y9Authorization> y9AuthorizationList = y9AuthorizationService
            .listByPrincipalTypeNotAndResourceId(AuthorizationPrincipalTypeEnum.ROLE.getValue(), resourceId);
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
     * @return
     */
    @RiseLog(operationName = "根据角色ID获取权限列表 ")
    @RequestMapping(value = "/listRelateResource")
    public Y9Page<AuthorizationVO> listRelateResource(@RequestParam("roleId") String roleId,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) {
        Page<Y9Authorization> y9AuthorizationPage = y9AuthorizationService.pageByPrincipalId(roleId, rows, page);
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        for (Y9Authorization y9Authorization : y9AuthorizationPage) {
            String resourceId = y9Authorization.getResourceId();
            Y9ResourceBase acResource = compositeResourceService.findById(resourceId);
            if (acResource == null) {
                try {
                    y9AuthorizationService.delete(y9Authorization);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
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
     * @return
     */
    @RiseLog(operationName = "根据资源id获取关联的角色列表 ")
    @RequestMapping(value = "/listRelateRole")
    public Y9Result<List<AuthorizationVO>> listRelateRole(@RequestParam("resourceId") String resourceId,
        String roleName, @RequestParam("authority") Integer authority) {
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();
        List<Y9Authorization> y9AuthorizationList;

        if (StringUtils.isNotBlank(roleName) && authority != null) {
            List<Y9Role> list = y9RoleService.listByName(roleName);
            List<String> roleIds = list.stream().map(Y9Role::getId).collect(Collectors.toList());
            if (!roleIds.isEmpty()) {
                y9AuthorizationList = y9AuthorizationService.listByRoleIds(roleIds, resourceId, authority);
            } else {
                y9AuthorizationList = new ArrayList<>();
            }
        } else {
            y9AuthorizationList = y9AuthorizationService
                .listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum.ROLE.getValue(), resourceId);
        }
        for (Y9Authorization y9Authorization : y9AuthorizationList) {
            if (AuthorizationPrincipalTypeEnum.ROLE.getValue().equals(y9Authorization.getPrincipalType())) {
                authorizationVOList.add(getAuthorizationVOForRole(y9Authorization));
            }
        }
        return Y9Result.success(authorizationVOList, "获取数据成功！");
    }

    /**
     * 移除角色授权许可记录
     *
     * @param ids 角色权限许可id数组
     * @return
     */
    @RiseLog(operationName = "移除角色授权许可记录", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam("ids") String[] ids) {
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
     * @return {@link Y9Result}<{@link String}>
     */
    @RiseLog(operationName = "保存管理资源权限许可对象 ", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(Integer authority, String principalId, Integer principalType,
        @RequestParam("resourceIds") String[] resourceIds) {
        y9AuthorizationService.save(authority, principalId, principalType, resourceIds);
        return Y9Result.successMsg("授权成功！");
    }

    /**
     * 保存资源授权管理关联组织信息
     *
     * @param authority 权限类型
     * @param resourceId 资源id
     * @param orgIds 组织节点id数组
     * @return
     */
    @RiseLog(operationName = "保存资源授权管理关联组织信息", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdateOrg")
    public Y9Result<String> saveOrUpdateOrg(Integer authority, String resourceId,
        @RequestParam("orgIds") String[] orgIds) {
        y9AuthorizationService.saveByOrg(authority, resourceId, orgIds);
        return Y9Result.successMsg("授权成功！");
    }

    /**
     * 保存关联角色权限许可对象
     *
     * @param authority 权限类型
     * @param resourceId 资源id
     * @param roleIds 角色id数组
     * @return
     */
    @RiseLog(operationName = "保存关联角色权限许可对象 ", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdateRole")
    public Y9Result<String> saveOrUpdateRole(Integer authority, String resourceId,
        @RequestParam("roleIds") String[] roleIds) {
        y9AuthorizationService.saveByRoles(authority, resourceId, roleIds);
        return Y9Result.successMsg("授权成功！");
    }

    /**
     * 根据资源ID，角色,和code模糊查询
     *
     * @param roleId 角色ID
     * @param resourceName 资源名
     * @param operationType 操作权限
     * @return
     */
    @RiseLog(operationName = "根据角色ID，资源名,和code模糊查询 ")
    @RequestMapping(value = "/searchOperations")
    public Y9Result<List<AuthorizationVO>> searchResourceOperations(@RequestParam("roleId") String roleId,
        String resourceName, @RequestParam("operationType") Integer operationType) {
        List<Y9ResourceBase> list = compositeResourceService.searchByName(resourceName);
        List<String> resourceIds = list.stream().map(Y9ResourceBase::getId).collect(Collectors.toList());
        List<Y9Authorization> y9AuthorizationList = new ArrayList<>();
        if (!resourceIds.isEmpty()) {
            y9AuthorizationList = y9AuthorizationService.listByResourceIds(resourceIds, roleId, operationType);
        }
        List<AuthorizationVO> authorizationVOList = new ArrayList<>();

        for (Y9Authorization authorization : y9AuthorizationList) {
            String resourceId = authorization.getResourceId();
            Y9ResourceBase acResource = compositeResourceService.findById(resourceId);
            if (acResource == null) {
                try {
                    y9AuthorizationService.delete(authorization);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
                continue;
            }
            authorizationVOList.add(getAuthorizationVOForResourceBase(authorization, acResource));
        }
        return Y9Result.success(authorizationVOList, "获取数据成功");
    }

}
