package net.risesoft.y9public.service.resource.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.OptionClassConsts;
import net.risesoft.entity.dictionary.Y9OptionValue;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.resource.DataCatalog;
import net.risesoft.repository.permission.Y9AuthorizationRepository;
import net.risesoft.repository.permission.cache.person.Y9PersonToResourceAndAuthorityRepository;
import net.risesoft.repository.permission.cache.position.Y9PositionToResourceAndAuthorityRepository;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.cache.Y9PersonToResourceAndAuthorityService;
import net.risesoft.service.permission.cache.Y9PositionToResourceAndAuthorityService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.CompositeResourceManager;
import net.risesoft.y9public.repository.resource.Y9DataCatalogRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;
import net.risesoft.y9public.service.resource.Y9DataCatalogService;

/**
 * 数据目录实现类
 *
 * @author shidaobang
 * @date 2024/10/09
 * @since 9.6.8
 */
@RequiredArgsConstructor
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@Slf4j
public class Y9DataCatalogServiceImpl implements Y9DataCatalogService {

    private final Y9PlatformProperties y9PlatformProperties;

    private final Y9DataCatalogRepository y9DataCatalogRepository;
    private final CompositeOrgBaseService compositeOrgBaseService;

    private final Y9PositionToResourceAndAuthorityService y9PositionToResourceAndAuthorityService;
    private final Y9PersonToResourceAndAuthorityService y9PersonToResourceAndAuthorityService;
    private final Y9OptionValueService y9OptionValueService;

    private final Y9TenantAppRepository y9TenantAppRepository;
    private final Y9AuthorizationRepository y9AuthorizationRepository;
    private final Y9PersonToResourceAndAuthorityRepository y9PersonToResourceAndAuthorityRepository;
    private final Y9PositionToResourceAndAuthorityRepository y9PositionToResourceAndAuthorityRepository;

    private final CompositeResourceManager compositeResourceManager;

    @Override
    public List<DataCatalog> getTree(String tenantId, String parentId, String treeType) {
        return this.getTree(tenantId, parentId, treeType, null, false, null, null);
    }

    @Override
    public DataCatalog getDataCatalogById(String id) {
        Y9DataCatalog y9DataCatalog = getById(id);
        return this.convertY9DataCatalogToDataCatalog(y9DataCatalog);
    }

    private Y9DataCatalog getById(String id) {
        return y9DataCatalogRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ResourceErrorCodeEnum.DATA_CATALOG_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = false)
    public Y9DataCatalog saveOrUpdate(Y9DataCatalog y9DataCatalog) {
        if (StringUtils.isNotBlank(y9DataCatalog.getId())) {
            Optional<Y9DataCatalog> y9DataCatalogOptional = y9DataCatalogRepository.findById(y9DataCatalog.getId());
            if (y9DataCatalogOptional.isPresent()) {
                Y9DataCatalog originY9DataCatalog = y9DataCatalogOptional.get();
                Y9BeanUtil.copyProperties(y9DataCatalog, originY9DataCatalog);

                if (StringUtils.isEmpty(originY9DataCatalog.getParentId())) {
                    originY9DataCatalog.setParentId(null);
                    originY9DataCatalog.setGuidPath(Y9OrgUtil.buildGuidPath(null, originY9DataCatalog.getId()));
                } else {
                    Y9DataCatalog parent = getById(y9DataCatalog.getParentId());
                    originY9DataCatalog
                        .setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), originY9DataCatalog.getId()));
                }

                return y9DataCatalogRepository.save(originY9DataCatalog);
            }
        }

        if (StringUtils.isBlank(y9DataCatalog.getId())) {
            y9DataCatalog.setId(Y9IdGenerator.genId());
        }

        y9DataCatalog.setTenantId(Y9LoginUserHolder.getTenantId());
        y9DataCatalog.setSystemId(InitDataConsts.SYSTEM_ID);
        y9DataCatalog.setInherit(Boolean.TRUE);
        y9DataCatalog.setTabIndex(this.getNextTabIndex(y9DataCatalog.getParentId()));

        if (StringUtils.isNotBlank(y9DataCatalog.getParentId())) {
            Y9DataCatalog parent = getById(y9DataCatalog.getParentId());
            y9DataCatalog.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), y9DataCatalog.getId()));
        } else {
            y9DataCatalog.setGuidPath(Y9OrgUtil.buildGuidPath(null, y9DataCatalog.getId()));
        }

        return y9DataCatalogRepository.save(y9DataCatalog);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9DataCatalog y9DataCatalog = getById(id);
        y9DataCatalogRepository.deleteById(id);

        // 删除租户与数据目录资源关联的数据
        List<Y9TenantApp> y9TenantAppList =
            y9TenantAppRepository.findByAppIdAndTenancy(y9DataCatalog.getAppId(), Boolean.TRUE);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            Y9LoginUserHolder.setTenantId(y9TenantApp.getTenantId());
            LOGGER.debug("删除租户[{}]与数据目录资源关联的数据", y9TenantApp.getTenantId());
            this.deleteTenantRelatedByDataCatalogId(id);
        }

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9DataCatalog));
    }

    @Override
    @Transactional(readOnly = false)
    public void saveByYears(Y9DataCatalog y9DataCatalog, Integer startYear, Integer endYear) {
        for (int year = startYear; year <= endYear; year++) {
            Optional<Y9DataCatalog> y9DataCatalogOptional = y9DataCatalogRepository.findByTenantIdAndParentIdAndName(
                Y9LoginUserHolder.getTenantId(), y9DataCatalog.getParentId(), String.valueOf(year));
            if (y9DataCatalogOptional.isEmpty()) {
                Y9DataCatalog yearDataCatalog = new Y9DataCatalog();
                Y9BeanUtil.copyProperties(y9DataCatalog, yearDataCatalog);
                yearDataCatalog.setName(String.valueOf(year));
                yearDataCatalog.setType(DataCatalogTypeEnum.YEAR);
                this.saveOrUpdate(yearDataCatalog);
            }
        }
    }

    @Override
    public List<DataCatalog> treeSearch(String tenantId, String name, String treeType) {
        return this.treeSearch(tenantId, name, treeType, null, null);
    }

    @Override
    public List<DataCatalog> getTree(String tenantId, String parentId, String treeType, Boolean enabled,
        boolean includeAllDescendant, AuthorityEnum authority, String personId) {
        List<Y9DataCatalog> y9DataCatalogList = new ArrayList<>();
        recursivelyGetTree(y9DataCatalogList, tenantId, parentId, treeType, enabled, includeAllDescendant, authority,
            personId);
        return this.convertY9DataCatalogToDataCatalog(y9DataCatalogList);
    }

    private void recursivelyGetTree(List<Y9DataCatalog> resultList, String tenantId, String parentId, String treeType,
        Boolean enabled, boolean includeAllDescendant, AuthorityEnum authority, String personId) {
        List<Y9DataCatalog> y9DataCatalogList;
        if (StringUtils.isBlank(parentId)) {
            // 根节点
            if (enabled == null) {
                y9DataCatalogList = y9DataCatalogRepository
                    .findByTenantIdAndParentIdIsNullAndTreeTypeOrderByTabIndex(tenantId, treeType);
            } else {
                y9DataCatalogList = y9DataCatalogRepository
                    .findByTenantIdAndParentIdIsNullAndTreeTypeAndEnabledOrderByTabIndex(tenantId, treeType, enabled);
            }
        } else {
            if (enabled == null) {
                y9DataCatalogList = y9DataCatalogRepository
                    .findByTenantIdAndParentIdAndTreeTypeOrderByTabIndexAsc(tenantId, parentId, treeType);
            } else {
                y9DataCatalogList =
                    y9DataCatalogRepository.findByTenantIdAndParentIdAndTreeTypeAndEnabledOrderByTabIndexAsc(tenantId,
                        parentId, treeType, enabled);
            }
        }

        if (authority != null) {
            y9DataCatalogList = y9DataCatalogList.stream()
                .filter(y9DataCatalog -> y9PersonToResourceAndAuthorityService.hasPermission(personId,
                    y9DataCatalog.getId(), authority))
                .collect(Collectors.toList());
        }

        resultList.addAll(y9DataCatalogList);

        if (includeAllDescendant) {
            for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
                recursivelyGetTree(resultList, tenantId, y9DataCatalog.getId(), treeType, enabled, includeAllDescendant,
                    authority, personId);
            }
        }
    }

    @Override
    public List<DataCatalog> treeSearch(String tenantId, String name, String treeType, AuthorityEnum authority,
        String personId) {
        List<DataCatalog> dataCatalogList = new ArrayList<>();

        Set<DataCatalog> y9DataCatalogResultSet = new HashSet<>();

        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndNameContainingAndTreeTypeOrderByTabIndex(tenantId, name, treeType);
        if (authority != null) {
            y9DataCatalogList = y9DataCatalogList.stream()
                .filter(y9DataCatalog -> y9PersonToResourceAndAuthorityService.hasPermission(personId,
                    y9DataCatalog.getId(), authority))
                .collect(Collectors.toList());
        }

        dataCatalogList.addAll(this.convertY9DataCatalogToDataCatalog(y9DataCatalogList));

        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            // FIXME: 向上递归遇到没有权限的数据目录如何处理？
            fillByUpwardRecursion(y9DataCatalogResultSet, y9DataCatalog.getParentId());
        }
        dataCatalogList.addAll(y9DataCatalogResultSet);
        return dataCatalogList;
    }

    @Override
    public List<Y9OptionValue> getTreeTypeList() {
        return y9OptionValueService.listByType(OptionClassConsts.DATA_CATALOG_TREE_TYPE);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveByType(Y9DataCatalog y9DataCatalog) {
        DataCatalogTypeEnum dataCatalogType = y9DataCatalog.getType();
        if (DataCatalogTypeEnum.ORG_UNIT.equals(dataCatalogType)) {
            recursivelySaveOrgUnitDataCatalog(y9DataCatalog.getParentId(), y9DataCatalog.getTreeType(), null);
            return;
        }

        List<String> dataCatalogPeriods = new ArrayList<>();
        if (DataCatalogTypeEnum.RETENTION_PERIOD.equals(dataCatalogType)) {
            dataCatalogPeriods = y9PlatformProperties.getDataCatalogRetentionPeriods();
        } else if (DataCatalogTypeEnum.CONFIDENTIALITY_PERIOD.equals(dataCatalogType)) {
            dataCatalogPeriods = y9PlatformProperties.getDataCatalogConfidentialityPeriods();
        }
        for (String dataCatalogPeriod : dataCatalogPeriods) {
            Y9DataCatalog retentionDataCatalog = new Y9DataCatalog();
            Y9BeanUtil.copyProperties(y9DataCatalog, retentionDataCatalog);
            retentionDataCatalog.setName(dataCatalogPeriod);
            this.saveOrUpdate(retentionDataCatalog);
        }
    }

    @Override
    public DataCatalog getTreeRoot(String id) {
        DataCatalog dataCatalog = getDataCatalogById(id);

        if (StringUtils.isNotBlank(dataCatalog.getParentId())) {
            return getTreeRoot(dataCatalog.getParentId());
        } else {
            return dataCatalog;
        }
    }

    @Override
    public List<Y9DataCatalog> listRoot() {
        return y9DataCatalogRepository.findByParentIdIsNull();
    }

    @Transactional(readOnly = false)
    public void recursivelySaveOrgUnitDataCatalog(String parentDataCatalogId, String treeType, String parentOrgUnitId) {
        List<Y9OrgBase> y9OrgBaseList = compositeOrgBaseService.listOrgUnitsAsParentByParentId(parentOrgUnitId);
        for (Y9OrgBase y9OrgBase : y9OrgBaseList) {
            Optional<Y9DataCatalog> y9DataCatalogOptional =
                y9DataCatalogRepository.findByTenantIdAndParentIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(),
                    parentDataCatalogId, y9OrgBase.getId());
            if (y9DataCatalogOptional.isEmpty()) {
                Y9DataCatalog orgUnitDataCatalog = new Y9DataCatalog();
                orgUnitDataCatalog.setName(y9OrgBase.getName());
                orgUnitDataCatalog.setOrgUnitId(y9OrgBase.getId());
                orgUnitDataCatalog.setParentId(parentDataCatalogId);
                orgUnitDataCatalog.setType(DataCatalogTypeEnum.ORG_UNIT);
                orgUnitDataCatalog.setTreeType(treeType);
                this.saveOrUpdate(orgUnitDataCatalog);
                recursivelySaveOrgUnitDataCatalog(orgUnitDataCatalog.getId(), treeType, y9OrgBase.getId());
            }
        }
    }

    private void fillByUpwardRecursion(Set<DataCatalog> y9DataCatalogResultSet, String parentId) {
        if (StringUtils.isNotBlank(parentId)) {
            DataCatalog dataCatalog = this.getDataCatalogById(parentId);
            y9DataCatalogResultSet.add(dataCatalog);
            fillByUpwardRecursion(y9DataCatalogResultSet, dataCatalog.getParentId());
        }
    }

    /**
     * 删除相关租户数据 <br>
     * 切换不同的数据源 需开启新事务
     *
     * @param dataCatalogId 数据目录id
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteTenantRelatedByDataCatalogId(String dataCatalogId) {
        y9AuthorizationRepository.deleteByResourceId(dataCatalogId);
        y9PersonToResourceAndAuthorityRepository.deleteByResourceId(dataCatalogId);
        y9PositionToResourceAndAuthorityRepository.deleteByResourceId(dataCatalogId);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDataCatalogDeleted(Y9EntityDeletedEvent<Y9DataCatalog> event) {
        Y9DataCatalog entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

    @Transactional(readOnly = false)
    public void deleteByParentId(String id) {
        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndParentId(Y9LoginUserHolder.getTenantId(), id);
        for (Y9DataCatalog subY9DataCatalog : y9DataCatalogList) {
            delete(subY9DataCatalog.getId());
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDepartmentUpdated(Y9EntityUpdatedEvent<Y9Department> event) {
        Y9Department updatedDepartment = event.getUpdatedEntity();

        List<Y9DataCatalog> y9DataCatalogList = y9DataCatalogRepository
            .findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), updatedDepartment.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setName(updatedDepartment.getName());
            this.saveOrUpdate(y9DataCatalog);
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDepartmentCreated(Y9EntityCreatedEvent<Y9Department> event) {
        Y9Department createdDepartment = event.getEntity();

        List<Y9DataCatalog> y9DataCatalogList = y9DataCatalogRepository
            .findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), createdDepartment.getParentId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            Y9DataCatalog orgUnitDataCatalog = new Y9DataCatalog();
            orgUnitDataCatalog.setName(createdDepartment.getName());
            orgUnitDataCatalog.setOrgUnitId(createdDepartment.getId());
            orgUnitDataCatalog.setParentId(y9DataCatalog.getId());
            orgUnitDataCatalog.setType(DataCatalogTypeEnum.ORG_UNIT);
            orgUnitDataCatalog.setTreeType(y9DataCatalog.getTreeType());
            this.saveOrUpdate(orgUnitDataCatalog);
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department entity = event.getEntity();
        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), entity.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setEnabled(Boolean.FALSE);
            this.saveOrUpdate(y9DataCatalog);
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onOrganizationUpdated(Y9EntityUpdatedEvent<Y9Organization> event) {
        Y9Organization updatedOrganization = event.getUpdatedEntity();

        List<Y9DataCatalog> y9DataCatalogList = y9DataCatalogRepository
            .findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), updatedOrganization.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setName(updatedOrganization.getName());
            this.saveOrUpdate(y9DataCatalog);
        }
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization entity = event.getEntity();
        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), entity.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setEnabled(Boolean.FALSE);
            this.saveOrUpdate(y9DataCatalog);
        }
    }

    private Integer getNextTabIndex(String parentId) {
        return y9DataCatalogRepository
            .findTopByTenantIdAndParentIdOrderByTabIndexDesc(Y9LoginUserHolder.getTenantId(), parentId)
            .map(Y9ResourceBase::getTabIndex)
            .orElse(-1) + 1;
    }

    private DataCatalog convertY9DataCatalogToDataCatalog(Y9DataCatalog y9DataCatalog) {
        DataCatalog dataCatalog = new DataCatalog();
        Y9BeanUtil.copyProperties(y9DataCatalog, dataCatalog);
        dataCatalog.setNodeType(y9DataCatalog.getResourceType().toString());
        return dataCatalog;
    }

    private List<DataCatalog> convertY9DataCatalogToDataCatalog(List<Y9DataCatalog> y9DataCatalogList) {
        List<DataCatalog> dataCatalogList = new ArrayList<>();
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            dataCatalogList.add(this.convertY9DataCatalogToDataCatalog(y9DataCatalog));
        }
        return dataCatalogList;
    }

}
