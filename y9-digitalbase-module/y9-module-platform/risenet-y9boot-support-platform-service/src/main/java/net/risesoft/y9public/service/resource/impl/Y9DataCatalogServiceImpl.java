package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.OptionClassConsts;
import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Organization;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.DataCatalogTypeEnum;
import net.risesoft.model.platform.dictionary.OptionValue;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.resource.DataCatalog;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.permission.cache.Y9PersonToResourceService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.manager.resource.Y9DataCatalogManager;
import net.risesoft.y9public.repository.resource.Y9DataCatalogRepository;
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
@Slf4j
public class Y9DataCatalogServiceImpl implements Y9DataCatalogService {

    private final Y9PlatformProperties y9PlatformProperties;

    private final Y9DataCatalogRepository y9DataCatalogRepository;
    private final Y9DataCatalogManager y9DataCatalogManager;

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9PersonToResourceService y9PersonToResourceService;
    private final Y9OptionValueService y9OptionValueService;

    @Override
    public DataCatalog getDataCatalogById(String id) {
        Y9DataCatalog y9DataCatalog = y9DataCatalogManager.getById(id);
        return this.convertY9DataCatalogToDataCatalog(y9DataCatalog);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public DataCatalog saveOrUpdate(DataCatalog dataCatalog) {
        Y9DataCatalog y9DataCatalog = PlatformModelConvertUtil.convert(dataCatalog, Y9DataCatalog.class);

        if (StringUtils.isNotBlank(y9DataCatalog.getId())) {
            Optional<Y9DataCatalog> y9DataCatalogOptional = y9DataCatalogManager.findById(y9DataCatalog.getId());
            if (y9DataCatalogOptional.isPresent()) {
                return PlatformModelConvertUtil.convert(y9DataCatalogManager.update(y9DataCatalog), DataCatalog.class);
            }
        }

        return PlatformModelConvertUtil.convert(y9DataCatalogManager.insert(y9DataCatalog), DataCatalog.class);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        Y9DataCatalog y9DataCatalog = y9DataCatalogManager.getById(id);
        y9DataCatalogRepository.deleteById(id);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9DataCatalog));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void saveByYears(DataCatalog dataCatalog, Integer startYear, Integer endYear) {
        for (int year = startYear; year <= endYear; year++) {
            Optional<Y9DataCatalog> y9DataCatalogOptional = y9DataCatalogRepository.findByTenantIdAndParentIdAndName(
                Y9LoginUserHolder.getTenantId(), dataCatalog.getParentId(), String.valueOf(year));
            if (y9DataCatalogOptional.isEmpty()) {
                DataCatalog yearDataCatalog = new DataCatalog();
                Y9BeanUtil.copyProperties(dataCatalog, yearDataCatalog);
                yearDataCatalog.setName(String.valueOf(year));
                yearDataCatalog.setType(DataCatalogTypeEnum.YEAR);
                this.saveOrUpdate(yearDataCatalog);
            }
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public List<DataCatalog> treeSearch(String tenantId, String name, String treeType) {
        return this.treeSearch(tenantId, name, treeType, null, null);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public List<DataCatalog> getTree(String tenantId, String parentId, String treeType) {
        return this.getTree(tenantId, parentId, treeType, null, false, null, null);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
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
                .filter(y9DataCatalog -> y9PersonToResourceService.hasPermission(personId, y9DataCatalog.getId(),
                    authority))
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
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public List<DataCatalog> treeSearch(String tenantId, String name, String treeType, AuthorityEnum authority,
        String personId) {
        List<DataCatalog> dataCatalogList = new ArrayList<>();

        Set<DataCatalog> y9DataCatalogResultSet = new HashSet<>();

        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndNameContainingAndTreeTypeOrderByTabIndex(tenantId, name, treeType);
        if (authority != null) {
            y9DataCatalogList = y9DataCatalogList.stream()
                .filter(y9DataCatalog -> y9PersonToResourceService.hasPermission(personId, y9DataCatalog.getId(),
                    authority))
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
    public List<OptionValue> getTreeTypeList() {
        return y9OptionValueService.listByType(OptionClassConsts.DATA_CATALOG_TREE_TYPE);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void saveByType(DataCatalog dataCatalog) {
        DataCatalogTypeEnum dataCatalogType = dataCatalog.getType();
        if (DataCatalogTypeEnum.ORG_UNIT.equals(dataCatalogType)) {
            recursivelySaveOrgUnitDataCatalog(dataCatalog.getParentId(), dataCatalog.getTreeType(), null);
            return;
        }

        List<String> dataCatalogPeriods = new ArrayList<>();
        if (DataCatalogTypeEnum.RETENTION_PERIOD.equals(dataCatalogType)) {
            dataCatalogPeriods = y9PlatformProperties.getDataCatalogRetentionPeriods();
        } else if (DataCatalogTypeEnum.CONFIDENTIALITY_PERIOD.equals(dataCatalogType)) {
            dataCatalogPeriods = y9PlatformProperties.getDataCatalogConfidentialityPeriods();
        }
        for (String dataCatalogPeriod : dataCatalogPeriods) {
            DataCatalog retentionDataCatalog = new DataCatalog();
            Y9BeanUtil.copyProperties(dataCatalog, retentionDataCatalog);
            retentionDataCatalog.setName(dataCatalogPeriod);
            this.saveOrUpdate(retentionDataCatalog);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public DataCatalog getTreeRoot(String id) {
        DataCatalog dataCatalog = getDataCatalogById(id);

        if (StringUtils.isNotBlank(dataCatalog.getParentId())) {
            return getTreeRoot(dataCatalog.getParentId());
        } else {
            return dataCatalog;
        }
    }

    @Override
    public List<DataCatalog> listRoot() {
        List<Y9DataCatalog> y9DataCatalogList = y9DataCatalogRepository.findByParentIdIsNull();
        return PlatformModelConvertUtil.convert(y9DataCatalogList, DataCatalog.class);
    }

    @Override
    public Optional<Y9DataCatalog> findByTreeTypeAndParentIdAndName(String treeType, String parentId,
        String dataCatalogName) {
        if (StringUtils.isBlank(parentId)) {
            return y9DataCatalogRepository.findByTenantIdAndTreeTypeAndParentIdIsNullAndName(
                Y9LoginUserHolder.getTenantId(), treeType, dataCatalogName);
        }

        return y9DataCatalogRepository.findByTenantIdAndParentIdAndName(Y9LoginUserHolder.getTenantId(), parentId,
            dataCatalogName);
    }

    @Override
    public List<DataCatalog> getAncestorList(String parentId) {
        List<DataCatalog> dataCatalogList = new ArrayList<>();
        String currentId = parentId;
        while (StringUtils.isNotBlank(currentId)) {
            DataCatalog dataCatalog = this.getDataCatalogById(currentId);
            dataCatalogList.add(dataCatalog);
            currentId = dataCatalog.getParentId();
        }
        return dataCatalogList;
    }

    private void recursivelySaveOrgUnitDataCatalog(String parentDataCatalogId, String treeType,
        String parentOrgUnitId) {
        List<OrgUnit> y9OrgBaseList = compositeOrgBaseService.listOrgUnitsAsParentByParentId(parentOrgUnitId);
        for (OrgUnit y9OrgBase : y9OrgBaseList) {
            Optional<Y9DataCatalog> y9DataCatalogOptional =
                y9DataCatalogRepository.findByTenantIdAndParentIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(),
                    parentDataCatalogId, y9OrgBase.getId());
            if (y9DataCatalogOptional.isEmpty()) {
                DataCatalog orgUnitDataCatalog = new DataCatalog();
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

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onDataCatalogDeleted(Y9EntityDeletedEvent<Y9DataCatalog> event) {
        Y9DataCatalog entity = event.getEntity();
        this.deleteByParentId(entity.getId());
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void deleteByParentId(String id) {
        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndParentId(Y9LoginUserHolder.getTenantId(), id);
        for (Y9DataCatalog subY9DataCatalog : y9DataCatalogList) {
            delete(subY9DataCatalog.getId());
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onDepartmentUpdated(Y9EntityUpdatedEvent<Y9Department> event) {
        Y9Department updatedDepartment = event.getUpdatedEntity();

        List<Y9DataCatalog> y9DataCatalogList = y9DataCatalogRepository
            .findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), updatedDepartment.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setName(updatedDepartment.getName());
            y9DataCatalogManager.update(y9DataCatalog);
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
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
            y9DataCatalogManager.insert(orgUnitDataCatalog);
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department entity = event.getEntity();
        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), entity.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setEnabled(Boolean.FALSE);
            y9DataCatalogManager.update(y9DataCatalog);
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onOrganizationUpdated(Y9EntityUpdatedEvent<Y9Organization> event) {
        Y9Organization updatedOrganization = event.getUpdatedEntity();

        List<Y9DataCatalog> y9DataCatalogList = y9DataCatalogRepository
            .findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), updatedOrganization.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setName(updatedOrganization.getName());
            y9DataCatalogManager.update(y9DataCatalog);
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onOrganizationDeleted(Y9EntityDeletedEvent<Y9Organization> event) {
        Y9Organization entity = event.getEntity();
        List<Y9DataCatalog> y9DataCatalogList =
            y9DataCatalogRepository.findByTenantIdAndOrgUnitId(Y9LoginUserHolder.getTenantId(), entity.getId());
        for (Y9DataCatalog y9DataCatalog : y9DataCatalogList) {
            y9DataCatalog.setEnabled(Boolean.FALSE);
            y9DataCatalogManager.update(y9DataCatalog);
        }
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
