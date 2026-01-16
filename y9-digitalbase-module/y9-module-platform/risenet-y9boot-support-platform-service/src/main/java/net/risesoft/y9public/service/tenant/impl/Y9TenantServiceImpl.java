package net.risesoft.y9public.service.tenant.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.tenant.Tenant;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantRepository;
import net.risesoft.y9public.repository.tenant.Y9TenantSystemRepository;
import net.risesoft.y9public.service.tenant.Y9TenantService;
import net.risesoft.y9public.service.user.Y9UserService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service(value = "tenantService")
@Slf4j
@RequiredArgsConstructor
public class Y9TenantServiceImpl implements Y9TenantService {

    private final Y9TenantRepository y9TenantRepository;
    private final Y9TenantSystemRepository y9TenantSystemRepository;
    private final Y9AppRepository y9AppRepository;

    private final Y9UserService y9UserService;

    private final Y9DataSourceManager y9DataSourceManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9SystemManager y9SystemManager;
    private final Y9TenantSystemManager y9TenantSystemManager;
    private final Y9TenantAppManager y9TenantAppManager;

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Tenant changeDisabled(String id) {
        Y9Tenant y9Tenant = y9TenantManager.getById(id);
        y9Tenant.setEnabled(!y9Tenant.getEnabled());
        return PlatformModelConvertUtil.y9TenantToTenant(y9TenantRepository.save(y9Tenant));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Tenant createTenant(String tenantName, String tenantShortName, String dataSourceId) {
        Tenant y9Tenant = new Tenant();
        y9Tenant.setName(tenantName);
        y9Tenant.setShortName(tenantShortName);
        y9Tenant.setEnabled(Boolean.TRUE);
        y9Tenant.setDefaultDataSourceId(dataSourceId);
        return this.saveOrUpdate(y9Tenant);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        y9UserService.deleteByTenantId(id);
        y9TenantRepository.deleteById(id);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    @Override
    public void deleteAfterCheck(String id) {
        long appCount = y9TenantSystemRepository.countByTenantId(id);
        Y9AssertUtil.isTrue(appCount == 0, TenantErrorCodeEnum.TENANT_HAS_REGISTERED_SYSTEM);

        this.delete(id);
    }

    @Override
    public Optional<Tenant> findById(String id) {
        return y9TenantRepository.findById(id).map(PlatformModelConvertUtil::y9TenantToTenant);
    }

    @Override
    public Optional<Tenant> findByShortName(String shortName) {
        return y9TenantRepository.findByShortName(shortName).map(PlatformModelConvertUtil::y9TenantToTenant);
    }

    @Override
    public Tenant getById(String id) {
        return PlatformModelConvertUtil.y9TenantToTenant(y9TenantManager.getById(id));
    }

    @Override
    public List<Tenant> listAll() {
        List<Y9Tenant> y9TenantList = y9TenantRepository.findAll(Sort.by(Direction.ASC, "createTime"));
        return PlatformModelConvertUtil.y9TenantToTenant(y9TenantList);
    }

    @Override
    public List<Tenant> listByGuidPathLike(String guidPath) {
        List<Y9Tenant> y9TenantList = y9TenantRepository.findByGuidPathContaining(guidPath);
        return PlatformModelConvertUtil.y9TenantToTenant(y9TenantList);
    }

    @Override
    public List<Tenant> listByParentIdAndTenantType(String parentId) {
        List<Y9Tenant> y9TenantList;
        if (StringUtils.isBlank(parentId)) {
            y9TenantList = y9TenantRepository.findByParentIdIsNullOrderByTabIndexAsc();
        } else {
            y9TenantList = y9TenantRepository.findByParentIdOrderByTabIndexAsc(parentId);
        }
        return PlatformModelConvertUtil.y9TenantToTenant(y9TenantList);
    }

    @Override
    public Optional<Tenant> findByTenantName(String tenantName) {
        return y9TenantRepository.findByName(tenantName).map(PlatformModelConvertUtil::y9TenantToTenant);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void move(String id, String parentId) {
        Tenant y9Tenant = this.getById(id);

        if (StringUtils.isNotBlank(parentId)) {
            List<Tenant> tenants = this.listByGuidPathLike(y9Tenant.getGuidPath());
            Set<String> tenantIdSet = tenants.stream().map(Tenant::getId).collect(Collectors.toSet());

            // 不能将租户移动到本身或子租户中
            Y9AssertUtil.notNull(tenantIdSet.contains(parentId), TenantErrorCodeEnum.MOVE_TO_SUB_TENANT_NOT_PERMITTED);
        }

        y9Tenant.setParentId(parentId);
        this.saveOrUpdate(y9Tenant);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Tenant saveOrUpdate(Tenant tenant) {
        Y9Tenant y9Tenant = PlatformModelConvertUtil.convert(tenant, Y9Tenant.class);

        Y9AssertUtil.isTrue(isNameAvailable(y9Tenant.getName(), y9Tenant.getId()),
            TenantErrorCodeEnum.NAME_HAS_BEEN_USED, y9Tenant.getName());
        Y9AssertUtil.isTrue(isShortNameAvailable(y9Tenant.getShortName(), y9Tenant.getId()),
            TenantErrorCodeEnum.SHORT_NAME_HAS_BEEN_USED, y9Tenant.getShortName());

        if (StringUtils.isNotBlank(y9Tenant.getId())) {
            Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findById(y9Tenant.getId());
            if (y9TenantOptional.isPresent()) {
                Y9Tenant originTenant = PlatformModelConvertUtil.convert(y9TenantOptional.get(), Y9Tenant.class);
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

                return PlatformModelConvertUtil.y9TenantToTenant(savedTenant);
            }
        }

        Y9Tenant savedTenant = y9TenantManager.insert(y9Tenant);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.TENANT_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.TENANT_CREATE.getDescription(), savedTenant.getName()))
            .objectId(savedTenant.getId())
            .oldObject(null)
            .currentObject(savedTenant)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return PlatformModelConvertUtil.y9TenantToTenant(savedTenant);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Tenant saveAndInitDataSource(Tenant tenant) {
        String tenantDataSourceId = null;
        if (StringUtils.isNotBlank(tenant.getId())) {
            Optional<Y9Tenant> y9TenantOptional = y9TenantRepository.findById(tenant.getId());
            if (y9TenantOptional.map(Y9Tenant::getDefaultDataSourceId).isPresent()) {
                tenantDataSourceId = y9TenantOptional.get().getDefaultDataSourceId();
            }
        }

        if (StringUtils.isBlank(tenantDataSourceId)) {
            tenantDataSourceId = Y9IdGenerator.genId();
            tenant.setDefaultDataSourceId(tenantDataSourceId);
        }

        Tenant savedTenant = this.saveOrUpdate(tenant);

        y9DataSourceManager.createDataSourceIfNotExists(savedTenant.getShortName(), null, tenantDataSourceId);

        return savedTenant;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void saveTenantOrders(String[] tenantIds) {
        for (int i = 0; i < tenantIds.length; i++) {
            Y9Tenant y9Tenant = y9TenantManager.getById(tenantIds[i]);
            y9Tenant.setTabIndex(i);
            y9TenantRepository.save(y9Tenant);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void register(String cnName, String enName) {
        String dataSourceId = Y9IdGenerator.genId();

        LOGGER.info("创建租户{}", cnName);
        Tenant tenant = this.createTenant(cnName, enName, dataSourceId);
        String tenantId = tenant.getId();

        LOGGER.info("创建数据库和数据源{}", enName);
        y9DataSourceManager.createDataSourceIfNotExists(tenant.getShortName(), null, dataSourceId);

        LOGGER.info("租用可自动租用的系统和应用");
        this.subscribeSystemsAndApps(tenantId);

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        y9DataSourceManager.dropTenantDefaultDataSource(dataSourceId);
                    }
                }
            });
        }
    }

    private void subscribeSystemsAndApps(String tenantId) {
        List<String> systemIds = y9SystemManager.listByAutoInit(true);
        String[] ids = systemIds.toArray(new String[systemIds.size()]);
        List<Y9TenantSystem> y9TenantSystems = y9TenantSystemManager.saveTenantSystems(ids, tenantId);
        for (Y9TenantSystem y9TenantSystem : y9TenantSystems) {
            List<Y9App> y9AppList = y9AppRepository
                .findBySystemIdAndAutoInitAndCheckedOrderByCreateTime(y9TenantSystem.getSystemId(), true, true);
            for (Y9App y9App : y9AppList) {
                y9TenantAppManager.save(y9App.getId(), tenantId, "默认租用");
            }
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
