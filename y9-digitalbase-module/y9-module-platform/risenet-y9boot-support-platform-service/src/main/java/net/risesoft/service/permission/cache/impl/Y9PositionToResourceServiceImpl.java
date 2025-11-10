package net.risesoft.service.permission.cache.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.entity.permission.cache.position.Y9PositionToResource;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.permission.cache.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.model.platform.permission.cache.IdentityToResourceBase;
import net.risesoft.model.platform.permission.cache.PositionToResource;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.permission.cache.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.service.permission.cache.Y9PositionToResourceService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9PlatformUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
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

    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final CompositeResourceManager compositeResourceManager;

    private final Y9PositionToResourceAndAuthorityManager y9PositionToResourceAndAuthorityManager;
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
        y9PositionToResourceAndAuthorityRepository.deleteByAuthorizationIdAndPositionId(authorizationId, positionId);
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
        y9PositionToResourceAndAuthorityRepository.deleteByPositionId(positionId);
    }

    @Override
    public boolean hasPermission(String positionId, String resourceId, AuthorityEnum authority) {
        return !y9PositionToResourceAndAuthorityRepository
            .findByPositionIdAndResourceIdAndAuthority(positionId, resourceId, authority)
            .isEmpty();
    }

    @Override
    public boolean hasPermissionByCustomId(String positionId, String customId, AuthorityEnum authority) {
        List<Y9ResourceBase> y9ResourceBaseList = compositeResourceManager.findByCustomId(customId);
        return y9ResourceBaseList.stream()
            .anyMatch(y9ResourceBase -> hasPermission(positionId, y9ResourceBase.getId(), authority));
    }

    @Override
    public List<PositionToResource> list(String positionId) {
        List<Y9PositionToResource> y9PositionToResourceList =
            y9PositionToResourceAndAuthorityRepository.findByPositionId(positionId);
        return entityToModel(y9PositionToResourceList);
    }

    private List<Y9PositionToResource> list(String positionId, String parentResourceId, AuthorityEnum authority) {
        if (StringUtils.isBlank(parentResourceId)) {
            List<Y9PositionToResource> list = new ArrayList<>();
            list.addAll(y9PositionToResourceAndAuthorityRepository
                .findByPositionIdAndParentResourceIdIsNullAndAuthority(positionId, authority));
            list.addAll(y9PositionToResourceAndAuthorityRepository
                .findByPositionIdAndParentResourceIdAndAuthority(positionId, "", authority));
            return list;
        }
        return y9PositionToResourceAndAuthorityRepository.findByPositionIdAndParentResourceIdAndAuthority(positionId,
            parentResourceId, authority);
    }

    @Override
    public List<PositionToResource> list(String positionId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<Y9PositionToResource> y9PositionToResourceList =
            y9PositionToResourceAndAuthorityRepository.findByPositionIdAndParentResourceIdAndAuthorityAndResourceType(
                positionId, parentResourceId, authority, resourceType);
        return entityToModel(y9PositionToResourceList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<App> listAppsByAuthority(String positionId, AuthorityEnum authority) {
        List<Y9PositionToResource> resourceList = y9PositionToResourceAndAuthorityRepository
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
    public Page<String> pageAppIdByAuthority(String positionId, AuthorityEnum authority, Y9PageQuery pageQuery) {
        return y9PositionToResourceAndAuthorityRepository.findResourceIdByPositionIdAndAuthorityAndResourceType(
            positionId, authority, ResourceTypeEnum.APP,
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("resourceId")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> listSubResources(String positionId, String resourceId, AuthorityEnum authority) {
        Set<Y9ResourceBase> returnResourceSet = new HashSet<>();
        List<Y9PositionToResource> personToResourceAndAuthorityList = this.list(positionId, resourceId, authority);
        for (Y9PositionToResource positionResource : personToResourceAndAuthorityList) {
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

    @EventListener
    @Transactional
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9PositionToResourceAndAuthorityRepository.deleteByPositionId(position.getId());
    }

    @EventListener
    @Transactional
    public void onAuthorizationDeleted(Y9EntityDeletedEvent<Y9Authorization> event) {
        Y9Authorization y9Authorization = event.getEntity();
        y9PositionToResourceAndAuthorityRepository.deleteByAuthorizationId(y9Authorization.getId());
    }

    @TransactionalEventListener
    public void onResourceDeleted(Y9EntityDeletedEvent<? extends Y9ResourceBase> event) {
        Y9ResourceBase entity = event.getEntity();
        for (String tenantId : Y9PlatformUtil.getTenantIds()) {
            deleteByResource(tenantId, entity);
        }
    }

    @Async
    protected void deleteByResource(String tenantId, Y9ResourceBase entity) {
        Y9LoginUserHolder.setTenantId(tenantId);
        y9PositionToResourceAndAuthorityRepository.deleteByResourceId(entity.getId());
        LOGGER.debug("{}资源[{}]删除时同步删除租户[{}]的岗位授权缓存数据", entity.getResourceType().getName(), entity.getId(), tenantId);
    }

    @EventListener
    public void onTenantAppDeleted(Y9EntityDeletedEvent<Y9TenantApp> event) {
        Y9TenantApp entity = event.getEntity();
        deleteByTenantApp(entity);
    }

    @Async
    protected void deleteByTenantApp(Y9TenantApp entity) {
        Y9LoginUserHolder.setTenantId(entity.getTenantId());
        List<Y9ResourceBase> y9ResourceList = compositeResourceManager.findByAppId(entity.getAppId());
        for (Y9ResourceBase y9ResourceBase : y9ResourceList) {
            y9PositionToResourceAndAuthorityRepository.deleteByResourceId(y9ResourceBase.getId());
        }
        LOGGER.debug("应用[{}]取消租用时同步删除租户[{}]的岗位授权缓存数据", entity.getAppId(), entity.getTenantId());
    }

    private List<PositionToResource> entityToModel(List<Y9PositionToResource> y9PositionToResourceList) {
        return PlatformModelConvertUtil.convert(y9PositionToResourceList, PositionToResource.class);
    }
}
