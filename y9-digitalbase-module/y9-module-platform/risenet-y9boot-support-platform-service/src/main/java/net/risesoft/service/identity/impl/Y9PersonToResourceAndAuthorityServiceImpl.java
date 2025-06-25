package net.risesoft.service.identity.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.Y9IdentityToResourceAndAuthorityBase;
import net.risesoft.entity.identity.person.Y9PersonToResourceAndAuthority;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.manager.identity.Y9PersonToResourceAndAuthorityManager;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.repository.identity.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.service.identity.Y9PersonToResourceAndAuthorityService;
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
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9PersonToResourceAndAuthorityServiceImpl implements Y9PersonToResourceAndAuthorityService {

    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9PersonToResourceAndAuthorityManager y9PersonToResourceAndAuthorityManager;
    private final CompositeResourceService compositeResourceService;
    private final Y9AppManager y9AppManager;
    private final Y9MenuManager y9MenuManager;
    private final Y9TenantAppManager y9TenantAppManager;

    @Override
    public void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgUnitId) {
        List<Y9Person> allPersons = compositeOrgBaseManager.listAllDescendantPersons(orgUnitId);
        for (Y9Person y9Person : allPersons) {
            this.deleteByAuthorizationIdAndPersonId(authorizationId, y9Person.getId());
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByAuthorizationIdAndPersonId(String authorizationId, String personId) {
        y9PersonToResourceAndAuthorityRepository.deleteByAuthorizationIdAndPersonId(authorizationId, personId);
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByOrgUnitId(String orgUnitId) {
        List<Y9Person> personList = compositeOrgBaseManager.listAllDescendantPersons(orgUnitId);
        for (Y9Person y9Person : personList) {
            deleteByPersonId(y9Person.getId());
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteByPersonId(String personId) {
        y9PersonToResourceAndAuthorityRepository.deleteByPersonId(personId);
    }

    @Override
    public boolean hasPermission(String personId, String resourceId, AuthorityEnum authority) {
        return !y9PersonToResourceAndAuthorityRepository
            .findByPersonIdAndResourceIdAndAuthority(personId, resourceId, authority).isEmpty();
    }

    @Override
    public boolean hasPermissionByCustomId(String personId, String resourceCustomId, AuthorityEnum authority) {
        List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.findByCustomId(resourceCustomId);
        return y9ResourceBaseList.stream()
            .anyMatch(y9ResourceBase -> hasPermission(personId, y9ResourceBase.getId(), authority));
    }

    @Override
    public List<Y9PersonToResourceAndAuthority> list(String personId) {
        return y9PersonToResourceAndAuthorityRepository.findByPersonId(personId);
    }

    @Override
    public List<Y9PersonToResourceAndAuthority> list(String personId, String parentResourceId,
        AuthorityEnum authority) {
        return y9PersonToResourceAndAuthorityRepository.findByPersonIdAndParentResourceIdAndAuthority(personId,
            parentResourceId, authority);
    }

    @Override
    public List<Y9PersonToResourceAndAuthority> list(String personId, String parentResourceId,
        ResourceTypeEnum resourceType, AuthorityEnum authority) {
        return y9PersonToResourceAndAuthorityRepository.findByPersonIdAndParentResourceIdAndAuthorityAndResourceType(
            personId, parentResourceId, authority, resourceType);
    }

    @Override
    public List<Y9App> listAppsByAuthority(String personId, AuthorityEnum authority) {
        List<Y9PersonToResourceAndAuthority> resourceList = y9PersonToResourceAndAuthorityRepository
            .findByPersonIdAndAuthorityAndResourceType(personId, authority, ResourceTypeEnum.APP);
        Set<Y9App> appSet = new HashSet<>();
        for (Y9PersonToResourceAndAuthority r : resourceList) {
            Optional<Y9TenantApp> y9TenantAppOptional = y9TenantAppManager
                .getByTenantIdAndAppIdAndTenancy(Y9LoginUserHolder.getTenantId(), r.getResourceId(), true);
            if (y9TenantAppOptional.isPresent()) {
                Y9App y9App = y9AppManager.getById(r.getResourceId());
                if (y9App.getEnabled()) {
                    appSet.add(y9App);
                }
            }
        }
        return appSet.stream().sorted().collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    @Override
    public void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Person person, Y9Authorization y9Authorization,
        Boolean inherit) {
        y9PersonToResourceAndAuthorityManager.saveOrUpdate(y9ResourceBase, person, y9Authorization, inherit);
    }

    @Override
    public Page<String> pageAppIdByAuthority(String personId, AuthorityEnum authority, Y9PageQuery pageQuery) {
        return y9PersonToResourceAndAuthorityRepository.findResourceIdByPersonIdAndAuthorityAndResourceType(personId,
            authority, ResourceTypeEnum.APP,
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by("resourceId")));
    }

    @Override
    public List<Y9ResourceBase> listSubResources(String personId, String resourceId, AuthorityEnum authority) {
        Set<Y9ResourceBase> returnResourceSet = new HashSet<>();
        List<Y9PersonToResourceAndAuthority> y9PersonToResourceAndAuthorityList =
            this.list(personId, resourceId, authority);

        for (Y9PersonToResourceAndAuthority personResource : y9PersonToResourceAndAuthorityList) {
            Y9ResourceBase y9ResourceBase = compositeResourceService
                .findByIdAndResourceType(personResource.getResourceId(), personResource.getResourceType());
            if (y9ResourceBase != null && y9ResourceBase.getEnabled()) {
                returnResourceSet.add(y9ResourceBase);
            }
        }
        return returnResourceSet.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Y9Menu> listSubMenus(String personId, String resourceId, AuthorityEnum authority) {
        List<Y9PersonToResourceAndAuthority> y9PersonToResourceAndAuthorityList =
            this.list(personId, resourceId, ResourceTypeEnum.MENU, authority);
        List<String> menuIdList = y9PersonToResourceAndAuthorityList.stream()
            .map(Y9IdentityToResourceAndAuthorityBase::getResourceId).distinct().collect(Collectors.toList());
        Set<Y9Menu> menuSet = new HashSet<>();
        for (String menuId : menuIdList) {
            Y9Menu y9Menu = y9MenuManager.getById(menuId);
            if (y9Menu.getEnabled()) {
                menuSet.add(y9Menu);
            }
        }
        return menuSet.stream().sorted().collect(Collectors.toList());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onPersonDeleted(Y9EntityDeletedEvent<Y9Person> event) {
        Y9Person person = event.getEntity();
        y9PersonToResourceAndAuthorityRepository.deleteByPersonId(person.getId());
    }
}
