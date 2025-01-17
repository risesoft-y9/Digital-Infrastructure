package net.risesoft.controller.identity;

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

import net.risesoft.controller.identity.vo.ResourcePermissionVO;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.identity.Y9IdentityToResourceAndAuthorityBase;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorizationPrincipalTypeEnum;
import net.risesoft.service.authorization.Y9AuthorizationService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.role.Y9Role;
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

    private List<ResourcePermissionVO.PermissionDetail> buildDetail(Y9ResourceBase y9ResourceBase,
                                                                    List<Y9IdentityToResourceAndAuthorityBase> permissionList) {
        return permissionList.stream().filter(permission -> permission.getResourceId().equals(y9ResourceBase.getId()))
                .map(permission -> {
                    ResourcePermissionVO.PermissionDetail detail = new ResourcePermissionVO.PermissionDetail();
                    detail.setAuthority(permission.getAuthority());
                    detail.setInherit(permission.getInherit());
                    Optional<Y9Authorization> y9AuthorizationOptional =
                            y9AuthorizationService.findById(permission.getAuthorizationId());
                    if (y9AuthorizationOptional.isPresent()) {
                        Y9Authorization y9Authorization = y9AuthorizationOptional.get();
                        detail.setPrincipalType(y9Authorization.getPrincipalType());
                        if (AuthorizationPrincipalTypeEnum.ROLE.equals(y9Authorization.getPrincipalType())) {
                            y9RoleService.findById(y9Authorization.getPrincipalId()).map(Y9Role::getName)
                                    .ifPresent(detail::setPrincipalName);
                        } else {
                            Optional<Y9OrgBase> y9OrgBaseOptional =
                                    compositeOrgBaseService.findOrgUnit(y9Authorization.getPrincipalId());
                            y9OrgBaseOptional.map(Y9OrgBase::getName).ifPresent(detail::setPrincipalName);
                        }
                    }
                    return detail;
                }).collect(Collectors.toList());
    }

    private ResourcePermissionVO buildPermissionVO(String systemId,
                                                   List<Y9IdentityToResourceAndAuthorityBase> permissionList) {
        ResourcePermissionVO permissionVO = new ResourcePermissionVO();
        permissionVO.setSystemCnName(y9SystemService.getById(systemId).getCnName());
        permissionVO.setResourceList(buildResourceList(permissionList));
        return permissionVO;
    }

    private void buildResourceList(List<Y9ResourceBase> y9ResourceBaseList, int level,
                                   List<Y9IdentityToResourceAndAuthorityBase> permissionList, Set<Y9ResourceBase> allResourceBaseList,
                                   List<ResourcePermissionVO.Resource> resourceList) {
        level++;
        for (Y9ResourceBase y9ResourceBase : y9ResourceBaseList) {
            ResourcePermissionVO.Resource resource = new ResourcePermissionVO.Resource();
            resource.setResourceName(y9ResourceBase.getName());
            resource.setLevel(level);
            resource.setResourceType(y9ResourceBase.getResourceType());
            resource.setPermissionDetailList(buildDetail(y9ResourceBase, permissionList));
            resource.setEnabled(y9ResourceBase.getEnabled());
            resourceList.add(resource);
            List<Y9ResourceBase> subResourceBaseList = allResourceBaseList.stream()
                    .filter(r -> y9ResourceBase.getId().equals(r.getParentId())).sorted().collect(Collectors.toList());
            buildResourceList(subResourceBaseList, level, permissionList, allResourceBaseList, resourceList);
        }
    }

    private List<ResourcePermissionVO.Resource>
    buildResourceList(List<Y9IdentityToResourceAndAuthorityBase> permissionList) {
        // 根据人员权限记录递归获取所有相关的资源
        Set<Y9ResourceBase> y9ResourceBaseList = getResourcesWithHierarchy(permissionList);
        Set<String> idSet = y9ResourceBaseList.stream().map(Y9ResourceBase::getId).collect(Collectors.toSet());
        List<Y9ResourceBase> topResourceList =
                y9ResourceBaseList.stream().filter(r -> (r.getParentId() == null || !idSet.contains(r.getParentId())))
                        .sorted().collect(Collectors.toList());

        List<ResourcePermissionVO.Resource> resourceList = new ArrayList<>();
        buildResourceList(topResourceList, -1, permissionList, y9ResourceBaseList, resourceList);
        return resourceList;
    }

    public List<ResourcePermissionVO>
    buildResourcePermissionVOList(List<Y9IdentityToResourceAndAuthorityBase> y9PersonToResourceAndAuthorityList) {
        List<ResourcePermissionVO> permissionVOList = new ArrayList<>();
        // 按系统分组
        Map<String, List<Y9IdentityToResourceAndAuthorityBase>> systemIdPermissionListMap =
                y9PersonToResourceAndAuthorityList.stream()
                        .collect(Collectors.groupingBy(Y9IdentityToResourceAndAuthorityBase::getSystemId));
        for (Map.Entry<String, List<Y9IdentityToResourceAndAuthorityBase>> entry : systemIdPermissionListMap
                .entrySet()) {
            permissionVOList.add(buildPermissionVO(entry.getKey(), entry.getValue()));
        }
        return permissionVOList;
    }

    private Set<Y9ResourceBase> getResourcesWithHierarchy(List<Y9IdentityToResourceAndAuthorityBase> permissionList) {
        Set<Y9ResourceBase> y9ResourceBaseSet = new HashSet<>();
        for (Y9IdentityToResourceAndAuthorityBase y9PersonToResourceAndAuthority : permissionList) {
            recursivelyGetResource(y9PersonToResourceAndAuthority.getResourceId(), y9ResourceBaseSet);
        }
        return y9ResourceBaseSet;
    }

    private void recursivelyGetResource(String resourceId, Set<Y9ResourceBase> y9ResourceBaseSet) {
        if (StringUtils.isNotBlank(resourceId)) {
            Y9ResourceBase y9ResourceBase = compositeResourceService.findById(resourceId);
            if (y9ResourceBase != null) {
                y9ResourceBaseSet.add(y9ResourceBase);
                recursivelyGetResource(y9ResourceBase.getParentId(), y9ResourceBaseSet);
            }
        }
    }

}
