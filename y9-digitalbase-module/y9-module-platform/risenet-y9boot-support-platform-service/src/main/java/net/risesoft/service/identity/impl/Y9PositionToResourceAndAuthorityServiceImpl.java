package net.risesoft.service.identity.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.Y9IdentityToResourceAndAuthorityBase;
import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.manager.identity.Y9PositionToResourceAndAuthorityManager;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.identity.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.service.identity.Y9PositionToResourceAndAuthorityService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.resource.Y9MenuManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.service.resource.CompositeResourceService;

/**
 * PositionToResourceAndAuthorityServiceImpl
 *
 * @author shidaobang
 * @author mengjuhua
 * @date 2022/4/6
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PositionToResourceAndAuthorityServiceImpl implements Y9PositionToResourceAndAuthorityService {

    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PositionToResourceAndAuthorityManager y9PositionToResourceAndAuthorityManager;
    private final CompositeResourceService compositeResourceService;
    private final Y9TenantAppManager y9TenantAppManager;
    private final Y9AppManager y9AppManager;
    private final Y9MenuManager y9MenuManager;

    @Transactional(readOnly = false)
    @Override
    public void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgId) {
        List<Y9Position> allPersons = compositeOrgBaseManager.listAllDescendantPositions(orgId);
        for (Y9Position y9Position : allPersons) {
            deleteByAuthorizationIdAndPositionId(authorizationId, y9Position.getId());
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByAuthorizationIdAndPositionId(String authorizationId, String positionId) {
        y9PositionToResourceAndAuthorityRepository.deleteByAuthorizationIdAndPositionId(authorizationId, positionId);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByOrgUnitId(String orgUnitId) {
        List<Y9Position> positionList = compositeOrgBaseManager.listAllDescendantPositions(orgUnitId);
        for (Y9Position y9Position : positionList) {
            deleteByPositionId(y9Position.getId());
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByPositionId(String positionId) {
        y9PositionToResourceAndAuthorityRepository.deleteByPositionId(positionId);
    }

    @Override
    public boolean hasPermission(String positionId, String resourceId, AuthorityEnum authority) {
        return !y9PositionToResourceAndAuthorityRepository
            .findByPositionIdAndResourceIdAndAuthority(positionId, resourceId, authority).isEmpty();
    }

    @Override
    public boolean hasPermissionByCustomId(String positionId, String customId, AuthorityEnum authority) {
        List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.findByCustomId(customId);
        return y9ResourceBaseList.stream()
            .anyMatch(y9ResourceBase -> hasPermission(positionId, y9ResourceBase.getId(), authority));
    }

    @Override
    public List<Y9PositionToResourceAndAuthority> list(String positionId) {
        return y9PositionToResourceAndAuthorityRepository.findByPositionId(positionId);
    }

    @Override
    public List<Y9PositionToResourceAndAuthority> list(String positionId, String parentResourceId,
        AuthorityEnum authority) {
        if (StringUtils.isBlank(parentResourceId)) {
            List<Y9PositionToResourceAndAuthority> list = new ArrayList<>();
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
    public List<Y9PositionToResourceAndAuthority> list(String positionId, String parentResourceId,
        ResourceTypeEnum resourceType, AuthorityEnum authority) {
        return y9PositionToResourceAndAuthorityRepository
            .findByPositionIdAndParentResourceIdAndAuthorityAndResourceType(positionId, parentResourceId, authority,
                resourceType);
    }

    @Override
    public List<Y9App> listAppsByAuthority(String positionId, AuthorityEnum authority) {
        List<Y9PositionToResourceAndAuthority> resourceList = y9PositionToResourceAndAuthorityRepository
            .findByPositionIdAndAuthorityAndResourceType(positionId, authority, ResourceTypeEnum.APP);
        Set<Y9App> appList = new HashSet<>();
        for (Y9PositionToResourceAndAuthority r : resourceList) {
            Optional<Y9TenantApp> y9TenantAppOptional = y9TenantAppManager
                .getByTenantIdAndAppIdAndTenancy(Y9LoginUserHolder.getTenantId(), r.getResourceId(), true);
            if (y9TenantAppOptional.isPresent()) {
                Y9App y9App = y9AppManager.getById(r.getResourceId());
                if (y9App.getEnabled()) {
                    appList.add(y9App);
                }
            }
        }
        return appList.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Y9PositionToResourceAndAuthority> listByPositionId(String positionId) {
        return y9PositionToResourceAndAuthorityRepository.findByPositionId(positionId);
    }

    @Transactional(readOnly = false)
    @Override
    public void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization,
        Boolean inherit) {
        y9PositionToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, y9Position, y9Authorization, inherit);
    }

    @Override
    public Page<String> pageAppIdByAuthority(String positionId, AuthorityEnum authority, Y9PageQuery pageQuery) {
        return y9PositionToResourceAndAuthorityRepository.findResourceIdByPositionIdAndAuthorityAndResourceType(
            positionId, authority, ResourceTypeEnum.APP,
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("resourceId")));
    }

    @Override
    public List<Y9ResourceBase> listSubResources(String positionId, String resourceId, AuthorityEnum authority) {
        Set<Y9ResourceBase> returnResourceSet = new HashSet<>();
        List<Y9PositionToResourceAndAuthority> personToResourceAndAuthorityList =
            this.list(positionId, resourceId, authority);
        for (Y9PositionToResourceAndAuthority positionResource : personToResourceAndAuthorityList) {
            Y9ResourceBase y9ResourceBase = compositeResourceService
                .findByIdAndResourceType(positionResource.getResourceId(), positionResource.getResourceType());
            if (y9ResourceBase != null && y9ResourceBase.getEnabled()) {
                returnResourceSet.add(y9ResourceBase);
            }
        }
        return returnResourceSet.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Y9Menu> listSubMenus(String positionId, String resourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority) {
        List<Y9PositionToResourceAndAuthority> personToResourceAndAuthorityList =
            this.list(positionId, resourceId, resourceType, authority);
        List<String> menuIdList = personToResourceAndAuthorityList.stream()
            .map(Y9IdentityToResourceAndAuthorityBase::getResourceId).distinct().collect(Collectors.toList());
        Set<Y9Menu> y9MenuSet = new HashSet<>();
        for (String menuId : menuIdList) {
            Y9Menu y9Menu = y9MenuManager.getById(menuId);
            if (y9Menu.getEnabled()) {
                y9MenuSet.add(y9Menu);
            }
        }
        return y9MenuSet.stream().sorted().collect(Collectors.toList());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPositionDeleted(Y9EntityDeletedEvent<Y9Position> event) {
        Y9Position position = event.getEntity();
        y9PositionToResourceAndAuthorityRepository.deleteByPositionId(position.getId());
    }

}
