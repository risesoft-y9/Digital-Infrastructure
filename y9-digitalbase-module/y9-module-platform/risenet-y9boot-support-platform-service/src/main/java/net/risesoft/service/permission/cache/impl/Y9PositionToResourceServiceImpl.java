package net.risesoft.service.permission.cache.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.cache.position.Y9PositionToResource;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.model.platform.permission.cache.IdentityToResourceBase;
import net.risesoft.model.platform.permission.cache.PositionToResource;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.repository.permission.cache.position.Y9PositionToResourceRepository;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.resource.Y9MenuManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;

/**
 * PositionToResourceAndAuthorityServiceImpl
 *
 * @author shidaobang
 * @author mengjuhua
 * @date 2022/4/6
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9PositionToResourceServiceImpl implements Y9PositionToResourceService {

    private final Y9PositionToResourceRepository y9PositionToResourceRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final CompositeResourceManager compositeResourceManager;

    private final Y9TenantAppManager y9TenantAppManager;
    private final Y9AppManager y9AppManager;
    private final Y9MenuManager y9MenuManager;

    @Transactional
    @Override
    public void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgId) {
        List<Y9Position> allPersons = compositeOrgBaseManager.listAllDescendantPositions(orgId);
        for (Y9Position y9Position : allPersons) {
            deleteByAuthorizationIdAndPositionId(authorizationId, y9Position.getId());
        }
    }

    @Transactional
    @Override
    public void deleteByAuthorizationIdAndPositionId(String authorizationId, String positionId) {
        y9PositionToResourceRepository.deleteByAuthorizationIdAndPositionId(authorizationId, positionId);
    }

    @Transactional
    @Override
    public void deleteByOrgUnitId(String orgUnitId) {
        List<Y9Position> positionList = compositeOrgBaseManager.listAllDescendantPositions(orgUnitId);
        for (Y9Position y9Position : positionList) {
            deleteByPositionId(y9Position.getId());
        }
    }

    @Transactional
    @Override
    public void deleteByPositionId(String positionId) {
        y9PositionToResourceRepository.deleteByPositionId(positionId);
    }

    @Override
    public boolean hasPermission(String positionId, String resourceId, AuthorityEnum authority) {
        return !y9PositionToResourceRepository
            .findByPositionIdAndResourceIdAndAuthority(positionId, resourceId, authority)
            .isEmpty();
    }

    @Override
    public boolean hasPermissionByCustomId(String positionId, String customId, AuthorityEnum authority) {
        Y9ResourceBase y9ResourceBase = compositeResourceManager.getByCustomId(customId);
        return hasPermission(positionId, y9ResourceBase.getId(), authority);
    }

    @Override
    public List<PositionToResource> list(String positionId) {
        List<Y9PositionToResource> y9PositionToResourceList =
            y9PositionToResourceRepository.findByPositionId(positionId);
        return entityToModel(y9PositionToResourceList);
    }

    private List<Y9PositionToResource> list2(String positionId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<Y9PositionToResource> list = new ArrayList<>();

        if (StringUtils.isBlank(parentResourceId)) {
            list.addAll(y9PositionToResourceRepository.findByPositionIdAndParentResourceIdIsNullAndAuthority(positionId,
                authority));
            list.addAll(y9PositionToResourceRepository.findByPositionIdAndParentResourceIdAndAuthority(positionId, "",
                authority));
        } else {
            list.addAll(y9PositionToResourceRepository.findByPositionIdAndParentResourceIdAndAuthority(positionId,
                parentResourceId, authority));
        }

        if (resourceType != null) {
            return list.stream()
                .filter(y9PositionToResource -> y9PositionToResource.getResourceType().equals(resourceType))
                .collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<PositionToResource> list(String positionId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<Y9PositionToResource> y9PositionToResourceList =
            y9PositionToResourceRepository.findByPositionIdAndParentResourceIdAndAuthorityAndResourceType(positionId,
                parentResourceId, authority, resourceType);
        return entityToModel(y9PositionToResourceList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<App> listAppsByAuthority(String positionId, AuthorityEnum authority) {
        List<Y9PositionToResource> resourceList = y9PositionToResourceRepository
            .findByPositionIdAndAuthorityAndResourceType(positionId, authority, ResourceTypeEnum.APP);
        Set<Y9App> appList = new HashSet<>();
        for (Y9PositionToResource r : resourceList) {
            Optional<Y9TenantApp> y9TenantAppOptional = y9TenantAppManager
                .getByTenantIdAndAppIdAndTenancy(Y9LoginUserHolder.getTenantId(), r.getResourceId(), true);
            if (y9TenantAppOptional.isPresent()) {
                Y9App y9App = y9AppManager.getByIdFromCache(r.getResourceId());
                if (!Boolean.FALSE.equals(y9App.getEnabled())) {
                    appList.add(y9App);
                }
            }
        }
        return appList.stream()
            .sorted()
            .map(y9App -> PlatformModelConvertUtil.convert(y9App, App.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByAuthorizationId(String authorizationId) {
        y9PositionToResourceRepository.deleteByAuthorizationId(authorizationId);
    }

    @Override
    public void deleteByResourceId(String resourceId) {
        y9PositionToResourceRepository.deleteByResourceId(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> listSubResources(String positionId, String resourceId, AuthorityEnum authority,
        ResourceTypeEnum resourceType) {
        Set<Y9ResourceBase> returnResourceSet = new HashSet<>();

        List<Y9PositionToResource> y9PositionToResourceList =
            this.list2(positionId, resourceId, resourceType, authority);
        for (Y9PositionToResource positionResource : y9PositionToResourceList) {
            Y9ResourceBase y9ResourceBase = compositeResourceManager
                .findByIdAndResourceType(positionResource.getResourceId(), positionResource.getResourceType());
            if (y9ResourceBase != null && y9ResourceBase.getEnabled()) {
                returnResourceSet.add(y9ResourceBase);
            }
        }
        return returnResourceSet.stream()
            .sorted()
            .map(PlatformModelConvertUtil::resourceBaseToResource)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> listSubResourcesByCustomId(String positionId, String customId, AuthorityEnum authority,
        ResourceTypeEnum resourceType) {
        Y9ResourceBase y9ResourceBase = compositeResourceManager.getByCustomId(customId);
        return this.listSubResources(positionId, y9ResourceBase.getId(), authority, resourceType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> listSubMenus(String positionId, String resourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<PositionToResource> personToResourceAndAuthorityList =
            this.list(positionId, resourceId, resourceType, authority);
        List<String> menuIdList = personToResourceAndAuthorityList.stream()
            .map(IdentityToResourceBase::getResourceId)
            .distinct()
            .collect(Collectors.toList());
        Set<Y9Menu> y9MenuSet = new HashSet<>();
        for (String menuId : menuIdList) {
            Y9Menu y9Menu = y9MenuManager.getByIdFromCache(menuId);
            if (!Boolean.FALSE.equals(y9Menu.getEnabled())) {
                y9MenuSet.add(y9Menu);
            }
        }
        return y9MenuSet.stream().sorted().map(PlatformModelConvertUtil::y9MenuToMenu).collect(Collectors.toList());
    }

    private List<PositionToResource> entityToModel(List<Y9PositionToResource> y9PositionToResourceList) {
        return PlatformModelConvertUtil.convert(y9PositionToResourceList, PositionToResource.class);
    }
}
