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

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.permission.cache.person.Y9PersonToResource;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.model.platform.permission.cache.PersonToResource;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.repository.permission.cache.person.Y9PersonToResourceRepository;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
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
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class Y9PersonToResourceServiceImpl implements Y9PersonToResourceService {

    private final Y9PersonToResourceRepository y9PersonToResourceRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final CompositeResourceManager compositeResourceManager;

    private final Y9AppManager y9AppManager;
    private final Y9MenuManager y9MenuManager;
    private final Y9TenantAppManager y9TenantAppManager;

    @Override
    @Transactional
    public void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgUnitId) {
        List<Y9Person> allPersons = compositeOrgBaseManager.listAllDescendantPersons(orgUnitId);
        for (Y9Person y9Person : allPersons) {
            this.deleteByAuthorizationIdAndPersonId(authorizationId, y9Person.getId());
        }
    }

    @Transactional
    @Override
    public void deleteByAuthorizationIdAndPersonId(String authorizationId, String personId) {
        y9PersonToResourceRepository.deleteByAuthorizationIdAndPersonId(authorizationId, personId);
    }

    @Transactional
    @Override
    public void deleteByOrgUnitId(String orgUnitId) {
        List<Y9Person> personList = compositeOrgBaseManager.listAllDescendantPersons(orgUnitId);
        for (Y9Person y9Person : personList) {
            deleteByPersonId(y9Person.getId());
        }
    }

    @Transactional
    @Override
    public void deleteByPersonId(String personId) {
        y9PersonToResourceRepository.deleteByPersonId(personId);
    }

    @Override
    public boolean hasPermission(String personId, String resourceId, AuthorityEnum authority) {
        return !y9PersonToResourceRepository.findByPersonIdAndResourceIdAndAuthority(personId, resourceId, authority)
            .isEmpty();
    }

    @Override
    public boolean hasPermissionByCustomId(String personId, String resourceCustomId, AuthorityEnum authority) {
        Y9ResourceBase y9ResourceBase = compositeResourceManager.getByCustomId(resourceCustomId);
        return hasPermission(personId, y9ResourceBase.getId(), authority);
    }

    @Override
    public List<PersonToResource> list(String personId) {
        List<Y9PersonToResource> y9PersonToResourceList = y9PersonToResourceRepository.findByPersonId(personId);
        return entityToModel(y9PersonToResourceList);
    }

    private List<Y9PersonToResource> list2(String personId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<Y9PersonToResource> list = new ArrayList<>();

        if (StringUtils.isEmpty(parentResourceId)) {
            list.addAll(
                y9PersonToResourceRepository.findByPersonIdAndParentResourceIdIsNullAndAuthority(personId, authority));
            list.addAll(
                y9PersonToResourceRepository.findByPersonIdAndParentResourceIdAndAuthority(personId, "", authority));
        } else {
            list.addAll(y9PersonToResourceRepository.findByPersonIdAndParentResourceIdAndAuthority(personId,
                parentResourceId, authority));
        }

        if (resourceType != null) {
            return list.stream()
                .filter(y9PersonToResource -> y9PersonToResource.getResourceType().equals(resourceType))
                .collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<PersonToResource> list(String personId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<Y9PersonToResource> y9PersonToResourceList =
            y9PersonToResourceRepository.findByPersonIdAndParentResourceIdAndAuthorityAndResourceType(personId,
                parentResourceId, authority, resourceType);
        return entityToModel(y9PersonToResourceList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<App> listAppsByAuthority(String personId, AuthorityEnum authority) {
        List<Y9PersonToResource> resourceList = y9PersonToResourceRepository
            .findByPersonIdAndAuthorityAndResourceType(personId, authority, ResourceTypeEnum.APP);
        Set<Y9App> appSet = new HashSet<>();
        for (Y9PersonToResource r : resourceList) {
            Optional<Y9TenantApp> y9TenantAppOptional = y9TenantAppManager
                .getByTenantIdAndAppIdAndTenancy(Y9LoginUserHolder.getTenantId(), r.getResourceId(), true);
            if (y9TenantAppOptional.isPresent()) {
                Y9App y9App = y9AppManager.getByIdFromCache(r.getResourceId());
                if (y9App.getEnabled()) {
                    appSet.add(y9App);
                }
            }
        }
        return appSet.stream()
            .sorted()
            .map(y9App -> PlatformModelConvertUtil.convert(y9App, App.class))
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByAuthorizationId(String authorizationId) {
        y9PersonToResourceRepository.deleteByAuthorizationId(authorizationId);
    }

    @Override
    public void deleteByResourceId(String resourceId) {
        y9PersonToResourceRepository.deleteByResourceId(resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> listSubResources(String personId, String resourceId, AuthorityEnum authority,
        ResourceTypeEnum resourceType) {
        Set<Y9ResourceBase> returnResourceSet = new HashSet<>();
        
        List<Y9PersonToResource> y9PersonToResourceList = this.list2(personId, resourceId, resourceType, authority);
        for (Y9PersonToResource personResource : y9PersonToResourceList) {
            Y9ResourceBase y9ResourceBase = compositeResourceManager
                .findByIdAndResourceType(personResource.getResourceId(), personResource.getResourceType());
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
    public List<Resource> listSubResourcesByCustomId(String personId, String customId, AuthorityEnum authority,
        ResourceTypeEnum resourceType) {
        Y9ResourceBase y9ResourceBase = compositeResourceManager.getByCustomId(customId);
        return this.listSubResources(personId, y9ResourceBase.getId(), authority, resourceType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Menu> listSubMenus(String personId, String resourceId, AuthorityEnum authority) {
        List<PersonToResource> y9PersonToResourceAndAuthorityList =
            this.list(personId, resourceId, ResourceTypeEnum.MENU, authority);
        List<String> menuIdList = y9PersonToResourceAndAuthorityList.stream()
            .map(PersonToResource::getResourceId)
            .distinct()
            .collect(Collectors.toList());
        Set<Y9Menu> menuSet = new HashSet<>();
        for (String menuId : menuIdList) {
            Y9Menu y9Menu = y9MenuManager.getByIdFromCache(menuId);
            if (y9Menu.getEnabled()) {
                menuSet.add(y9Menu);
            }
        }
        return menuSet.stream().sorted().map(PlatformModelConvertUtil::y9MenuToMenu).collect(Collectors.toList());
    }

    private List<PersonToResource> entityToModel(List<Y9PersonToResource> y9PersonToResourceList) {
        return PlatformModelConvertUtil.convert(y9PersonToResourceList, PersonToResource.class);
    }

}
