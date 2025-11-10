package net.risesoft.controller.permission.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.permission.Authorization;
import net.risesoft.model.platform.permission.cache.IdentityToResourceBase;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.Y9AuthorizationService;
import net.risesoft.vo.permission.ResourcePermissionVO;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 构建 资源权限Vo 集合
 *
 * @author shidaobang
 * @date 2023/07/05
 * @since 9.6.2
 */
@Component
@RequiredArgsConstructor
public class ResourcePermissionVOBuilder {

    private final CompositeResourceService compositeResourceService;
    private final CompositeOrgBaseService compositeOrgBaseService;

    private final Y9AuthorizationService y9AuthorizationService;
    private final Y9RoleService y9RoleService;
    private final Y9SystemService y9SystemService;

    private List<ResourcePermissionVO.PermissionDetail> buildDetail(Resource resource,
        List<IdentityToResourceBase> permissionList) {
        return permissionList.stream()
            .filter(permission -> permission.getResourceId().equals(resource.getId()))
            .map(permission -> {
                ResourcePermissionVO.PermissionDetail detail = new ResourcePermissionVO.PermissionDetail();
                detail.setAuthority(permission.getAuthority());
                detail.setInherit(permission.getInherit());
                Optional<Authorization> authorizationOptional =
                    y9AuthorizationService.findById(permission.getAuthorizationId());
                if (authorizationOptional.isPresent()) {
                    Authorization authorization = authorizationOptional.get();
                    detail.setPrincipalType(authorization.getPrincipalType());
                    if (AuthorizationPrincipalTypeEnum.ROLE.equals(authorization.getPrincipalType())) {
                        y9RoleService.findById(authorization.getPrincipalId())
                            .map(Role::getName)
                            .ifPresent(detail::setPrincipalName);
                    } else {
                        Optional<OrgUnit> orgUnitOptional =
                            compositeOrgBaseService.findOrgUnit(authorization.getPrincipalId());
                        orgUnitOptional.map(OrgUnit::getName).ifPresent(detail::setPrincipalName);
                    }
                }
                return detail;
            })
            .collect(Collectors.toList());
    }

    private ResourcePermissionVO buildPermissionVO(String systemId, List<IdentityToResourceBase> permissionList) {
        ResourcePermissionVO permissionVO = new ResourcePermissionVO();
        permissionVO.setSystemCnName(y9SystemService.getById(systemId).getCnName());
        permissionVO.setResourceList(buildResourceList(permissionList));
        return permissionVO;
    }

    private void buildResourceList(List<Resource> resourceList, int level, List<IdentityToResourceBase> permissionList,
        Set<Resource> allResourceBaseList, List<ResourcePermissionVO.Resource> permittedResourceList) {
        level++;
        for (Resource resource : resourceList) {
            ResourcePermissionVO.Resource permittedResource = new ResourcePermissionVO.Resource();
            permittedResource.setResourceName(resource.getName());
            permittedResource.setLevel(level);
            permittedResource.setResourceType(resource.getResourceType());
            permittedResource.setPermissionDetailList(buildDetail(resource, permissionList));
            permittedResource.setEnabled(resource.getEnabled());
            permittedResourceList.add(permittedResource);
            List<Resource> subResourceBaseList = allResourceBaseList.stream()
                .filter(r -> resource.getId().equals(r.getParentId()))
                .sorted()
                .collect(Collectors.toList());
            buildResourceList(subResourceBaseList, level, permissionList, allResourceBaseList, permittedResourceList);
        }
    }

    private List<ResourcePermissionVO.Resource> buildResourceList(List<IdentityToResourceBase> permissionList) {
        // 根据人员权限记录递归获取所有相关的资源
        Set<Resource> resourceSet = getResourcesWithHierarchy(permissionList);
        Set<String> idSet = resourceSet.stream().map(Resource::getId).collect(Collectors.toSet());
        List<Resource> topResourceList = resourceSet.stream()
            .filter(r -> (r.getParentId() == null || !idSet.contains(r.getParentId())))
            .sorted()
            .collect(Collectors.toList());

        List<ResourcePermissionVO.Resource> permittedResourceList = new ArrayList<>();
        buildResourceList(topResourceList, -1, permissionList, resourceSet, permittedResourceList);
        return permittedResourceList;
    }

    public List<ResourcePermissionVO> buildResourcePermissionVOList(List<IdentityToResourceBase> permissionList) {
        List<ResourcePermissionVO> permissionVOList = new ArrayList<>();
        // 按系统分组
        Map<String, List<IdentityToResourceBase>> systemIdPermissionListMap =
            permissionList.stream().collect(Collectors.groupingBy(IdentityToResourceBase::getSystemId));
        for (Map.Entry<String, List<IdentityToResourceBase>> entry : systemIdPermissionListMap.entrySet()) {
            permissionVOList.add(buildPermissionVO(entry.getKey(), entry.getValue()));
        }
        return permissionVOList;
    }

    private Set<Resource> getResourcesWithHierarchy(List<IdentityToResourceBase> permissionList) {
        Set<Resource> resourceSet = new HashSet<>();
        for (IdentityToResourceBase identityToResourceBase : permissionList) {
            recursivelyGetResource(identityToResourceBase.getResourceId(), resourceSet);
        }
        return resourceSet;
    }

    private void recursivelyGetResource(String resourceId, Set<Resource> resourceSet) {
        if (StringUtils.isNotBlank(resourceId)) {
            Resource resource = compositeResourceService.findById(resourceId);
            if (resource != null) {
                resourceSet.add(resource);
                recursivelyGetResource(resource.getParentId(), resourceSet);
            }
        }
    }

}
