package net.risesoft.y9public.service.tenant.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.repository.tenant.Y9TenantRepository;
import net.risesoft.y9public.service.tenant.Y9TenantService;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "tenantService")
@Transactional(value = "rsPublicTransactionManager")
@Slf4j
@RequiredArgsConstructor
public class Y9TenantServiceImpl implements Y9TenantService {

    private final Y9TenantRepository y9TenantRepository;

    private final Y9UserService y9UserService;

    private final Y9DataSourceManager y9DataSourceManager;
    private final Y9TenantManager y9TenantManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant changDefaultDataSourceId(String id, String datasourceId) {
        Y9Tenant y9Tenant = this.getById(id);
        y9Tenant.setDefaultDataSourceId(datasourceId);
        return y9TenantRepository.save(y9Tenant);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant changeDisabled(String id) {
        Y9Tenant y9Tenant = this.getById(id);
        y9Tenant.setEnabled(!y9Tenant.getEnabled());
        return y9TenantRepository.save(y9Tenant);
    }

    @Override
    public long countByShortName(String shortName) {
        return y9TenantRepository.countByShortName(shortName);
    }

    @Override
    public long countByShortNameAndIdIsNot(String shortName, String tenantId) {
        return y9TenantRepository.countByShortNameAndIdIsNot(shortName, tenantId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant createTenant(String tenantName, String tenantShortName, String dataSourceId) {
        Y9Tenant y9Tenant = new Y9Tenant();
        y9Tenant.setName(tenantName);
        y9Tenant.setShortName(tenantShortName);
        y9Tenant.setEnabled(Boolean.TRUE);
        y9Tenant.setDefaultDataSourceId(dataSourceId);
        return this.saveOrUpdate(y9Tenant);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9UserService.deleteByTenantId(id);
        y9TenantRepository.deleteById(id);
    }

    @Override
    public Optional<Y9Tenant> findById(String id) {
        return y9TenantRepository.findById(id);
    }

    @Override
    public Optional<Y9Tenant> findByShortName(String shortName) {
        return y9TenantRepository.findByShortName(shortName);
    }

    @Override
    public Y9Tenant getById(String id) {
        return y9TenantManager.getById(id);
    }

    @Override
    public List<Y9Tenant> listAll() {
        return y9TenantRepository.findAll(Sort.by(Direction.ASC, "createTime"));
    }

    @Override
    public List<Y9Tenant> listByGuidPathLike(String guidPath) {
        return y9TenantRepository.findByGuidPathContaining(guidPath);
    }

    @Override
    public List<Y9Tenant> listByParentIdAndTenantType(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return y9TenantRepository.findByParentIdIsNullOrderByTabIndexAsc();
        }
        return y9TenantRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    @Override
    public Optional<Y9Tenant> findByTenantName(String tenantName) {
        return y9TenantRepository.findByName(tenantName);
    }

    @Override
    @Transactional(readOnly = false)
    public void move(String id, String parentId) {
        Y9Tenant y9Tenant = this.getById(id);

        if (StringUtils.isNotBlank(parentId)) {
            List<Y9Tenant> tenants = this.listByGuidPathLike(y9Tenant.getGuidPath());
            Set<String> tenantIdSet = tenants.stream().map(Y9Tenant::getId).collect(Collectors.toSet());

            // 不能将租户移动到本身或子租户中
            Y9Assert.notNull(tenantIdSet.contains(parentId), TenantErrorCodeEnum.MOVE_TO_SUB_TENANT_NOT_PERMITTED);
        }

        y9Tenant.setParentId(parentId);
        this.saveOrUpdate(y9Tenant);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant saveOrUpdate(Y9Tenant y9Tenant) {
        Y9Assert.isTrue(isNameAvailable(y9Tenant.getName(), y9Tenant.getId()), TenantErrorCodeEnum.NAME_HAS_BEEN_USED,
            y9Tenant.getName());
        Y9Assert.isTrue(isShortNameAvailable(y9Tenant.getShortName(), y9Tenant.getId()),
            TenantErrorCodeEnum.SHORT_NAME_HAS_BEEN_USED, y9Tenant.getShortName());

        if (StringUtils.isNotBlank(y9Tenant.getId())) {
            Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findById(y9Tenant.getId());
            if (y9TenantOptional.isPresent()) {
                Y9Tenant originTenant = Y9ModelConvertUtil.convert(y9TenantOptional.get(), Y9Tenant.class);
                Y9Tenant savedTenant = y9TenantManager.update(y9Tenant);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.TENANT_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.TENANT_UPDATE.getDescription(), savedTenant.getName()))
                    .objectId(savedTenant.getId())
                    .oldObject(originTenant)
                    .currentObject(savedTenant)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedTenant;
            }
        }

        Y9Tenant savedY9Tenant = y9TenantManager.insert(y9Tenant);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.TENANT_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.TENANT_CREATE.getDescription(), savedY9Tenant.getName()))
            .objectId(savedY9Tenant.getId())
            .oldObject(null)
            .currentObject(savedY9Tenant)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedY9Tenant;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Tenant saveAndInitDataSource(Y9Tenant y9Tenant) {
        Y9Tenant savedY9Tenant = this.saveOrUpdate(y9Tenant);

        if (StringUtils.isBlank(savedY9Tenant.getDefaultDataSourceId())) {
            Y9DataSource y9DataSource = null;
            try {
                y9DataSource = y9DataSourceManager.createTenantDefaultDataSource(y9Tenant.getShortName(), null);
                y9Tenant.setDefaultDataSourceId(y9DataSource.getId());
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
                if (y9DataSource != null) {
                    y9DataSourceManager.dropTenantDefaultDataSource(y9DataSource.getId(), y9Tenant.getShortName());
                }
            }
            return this.saveOrUpdate(y9Tenant);
        }
        return savedY9Tenant;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveTenantOrders(String[] tenantIds) {
        for (int i = 0; i < tenantIds.length; i++) {
            Y9Tenant y9Tenant = this.getById(tenantIds[i]);
            y9Tenant.setTabIndex(i);
            y9TenantRepository.save(y9Tenant);
        }
    }

    private boolean isNameAvailable(String name, String id) {
        Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findByName(name);

        if (y9TenantOptional.isEmpty()) {
            // 不存在同名的租户肯定可用
            return true;
        }

        // 编辑租户时没修改中文名称同样认为可用
        return y9TenantOptional.get().getId().equals(id);
    }

    private boolean isShortNameAvailable(String shortName, String id) {
        Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findByShortName(shortName);

        if (y9TenantOptional.isEmpty()) {
            // 不存在同名的租户肯定可用
            return true;
        }

        // 编辑租户时没修改英文名称同样认为可用
        return y9TenantOptional.get().getId().equals(id);
    }

}
