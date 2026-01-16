package net.risesoft.y9public.service.resource.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.DefaultConsts;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.ResourceErrorCodeEnum;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9AssertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.Y9System;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.Y9SystemRepository;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.specification.Y9AppSpecification;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9AppServiceImpl implements Y9AppService {

    protected final Y9TenantAppManager y9TenantAppManager;
    private final Y9AppManager y9AppManager;
    private final Y9TenantSystemManager y9TenantSystemManager;

    private final Y9AppRepository y9AppRepository;
    private final Y9SystemRepository y9SystemRepository;

    private static List<App> entityToModel(List<Y9App> y9AppList) {
        return PlatformModelConvertUtil.convert(y9AppList, App.class);
    }

    private static App entityToModel(Y9App y9App) {
        return PlatformModelConvertUtil.convert(y9App, App.class);
    }

    @Override
    public boolean existBySystemIdAndName(final String systemId, final String appName) {
        List<Y9App> appList = y9AppRepository.findBySystemIdAndName(systemId, appName);
        return appList.isEmpty();
    }

    @Override
    public boolean existBySystemIdAndUrl(final String systemId, final String url) {
        List<Y9App> appList = y9AppRepository.findBySystemIdAndUrl(systemId, url);
        return appList.isEmpty();
    }

    @Override
    public Optional<App> findBySystemIdAndCustomId(String systemId, String customId) {
        return y9AppRepository.findBySystemIdAndCustomId(systemId, customId).map(PlatformModelConvertUtil::y9AppToApp);
    }

    @Override
    public Optional<App> findBySystemNameAndCustomId(String systemName, String customId) {
        Optional<Y9System> y9SystemOptional = y9SystemRepository.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            return y9AppRepository.findBySystemIdAndCustomId(y9SystemOptional.get().getId(), customId)
                .map(PlatformModelConvertUtil::y9AppToApp);
        }
        return Optional.empty();
    }

    @Override
    public List<App> listAll() {
        List<Y9App> y9AppList = y9AppRepository.findAll(Sort.by("systemId", "tabIndex"));
        return PlatformModelConvertUtil.y9AppToApp(y9AppList);
    }

    @Override
    public List<App> listByEnable() {
        List<Y9App> y9AppList = y9AppRepository.findByEnabledOrderByTabIndex(true);
        return PlatformModelConvertUtil.y9AppToApp(y9AppList);
    }

    @Override
    public List<App> listByIds(List<String> appIdList) {
        List<Y9App> y9AppList = y9AppRepository.findAllById(appIdList);
        return entityToModel(y9AppList);
    }

    @Override
    public List<App> listByAppName(String appName) {
        if (appName != null && !appName.isEmpty()) {
            return PlatformModelConvertUtil.y9AppToApp(y9AppRepository.findByName(appName));
        }
        return Collections.emptyList();
    }

    @Override
    public List<App> listByAutoInitAndChecked(Boolean autoInit, Boolean checked) {
        return entityToModel(y9AppRepository.findByAutoInitAndCheckedOrderByCreateTime(autoInit, checked));
    }

    @Override
    public List<App> listByChecked(boolean checked) {
        List<Y9App> y9AppList = y9AppRepository.findByCheckedOrderByCreateTime(checked);
        return entityToModel(y9AppList);
    }

    @Override
    public List<App> listByCustomId(String customId) {
        List<Y9App> y9AppList = y9AppRepository.findByCustomId(customId);
        return entityToModel(y9AppList);
    }

    @Override
    public List<App> listBySystemId(String systemId) {
        List<Y9App> y9AppList = y9AppRepository.findBySystemIdOrderByTabIndex(systemId);
        return entityToModel(y9AppList);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public List<App> listBySystemName(String systemName) {
        Optional<Y9System> y9SystemOptional = y9SystemRepository.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            List<Y9App> y9AppList = y9AppRepository.findBySystemId(y9SystemOptional.get().getId());
            return entityToModel(y9AppList);
        }
        return new ArrayList<>();
    }

    @Override
    public Y9Page<App> page(Y9PageQuery pageQuery, String systemId, String name) {
        Y9AppSpecification specification = new Y9AppSpecification(systemId, name);
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(),
            Sort.by(Sort.Direction.ASC, "tabIndex").and(Sort.by(Sort.Direction.DESC, "createTime")));
        Page<Y9App> y9AppPage = y9AppRepository.findAll(specification, pageRequest);
        return Y9Page.success(pageQuery.getPage(), y9AppPage.getTotalPages(), y9AppPage.getTotalElements(),
            entityToModel(y9AppPage.getContent()));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App saveAndRegister4Tenant(App app) {
        App savedApp = this.saveOrUpdate(app);
        // 审核应用
        this.verifyApp(savedApp.getId(), true, Y9LoginUserHolder.getUserInfo().getName());
        if (Y9LoginUserHolder.getUserInfo().getManagerLevel().isTenantManager()) {
            // 租用系统
            y9TenantSystemManager.saveTenantSystem(savedApp.getSystemId(), Y9LoginUserHolder.getTenantId());
            // 租用应用
            y9TenantAppManager.save(savedApp.getId(), Y9LoginUserHolder.getTenantId(), "系统默认租用");
        }
        return savedApp;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App saveIsvApp(App app) {
        if (app.getTabIndex() == null || DefaultConsts.TAB_INDEX.equals(app.getTabIndex())) {
            Integer tabIndex =
                y9AppRepository.findTopByOrderByTabIndexDesc().map(y9App -> y9App.getTabIndex() + 1).orElse(1);
            app.setTabIndex(tabIndex);
        }

        return saveOrUpdate(app);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void saveOrder(String[] appIds) {
        if (appIds.length > 0) {
            for (int i = 0, len = appIds.length; i < len; i++) {
                Y9App app = y9AppManager.getById(appIds[i]);
                app.setTabIndex(i + 1);
                y9AppManager.update(app);
            }
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App verifyApp(String id, boolean checked, String verifyUserName) {
        Y9App y9App = y9AppManager.getById(id);
        y9App.setChecked(checked);
        y9App.setVerifyUserName(verifyUserName);
        return entityToModel(y9AppManager.update(y9App));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(List<String> idList) {
        for (String id : idList) {
            this.delete(id);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void delete(String id) {
        Y9App y9App = y9AppManager.getById(id);
        y9AppManager.delete(id);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.APP_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.APP_DELETE.getDescription(), y9App.getName()))
            .objectId(id)
            .oldObject(y9App)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    @Override
    public void deleteAfterCheck(String id) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppManager.listByAppIdAndTenancy(id, Boolean.TRUE);
        Y9AssertUtil.isEmpty(y9TenantAppList, ResourceErrorCodeEnum.APP_IS_REGISTERED_BY_TENANT, id);

        this.delete(id);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public List<App> disable(List<String> idList) {
        List<App> y9AppList = new ArrayList<>();
        for (String id : idList) {
            y9AppList.add(this.disable(id));
        }
        return y9AppList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App disable(String id) {
        Y9App currentApp = y9AppManager.getById(id);
        Y9App originalApp = PlatformModelConvertUtil.convert(currentApp, Y9App.class);

        currentApp.setEnabled(Boolean.FALSE);
        Y9App savedApp = y9AppManager.update(currentApp);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.APP_UPDATE_ENABLE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.APP_UPDATE_ENABLE.getDescription(), savedApp.getName(), "禁用"))
            .objectId(id)
            .oldObject(originalApp)
            .currentObject(savedApp)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedApp);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public List<App> enable(List<String> idList) {
        List<App> y9AppList = new ArrayList<>();
        for (String id : idList) {
            y9AppList.add(this.enable(id));
        }
        return y9AppList;
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App enable(String id) {
        Y9App currentApp = y9AppManager.getById(id);
        Y9App originalApp = PlatformModelConvertUtil.convert(currentApp, Y9App.class);

        currentApp.setEnabled(Boolean.TRUE);
        Y9App savedApp = y9AppManager.update(currentApp);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.APP_UPDATE_ENABLE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.APP_UPDATE_ENABLE.getDescription(), savedApp.getName(), "启用"))
            .objectId(id)
            .oldObject(originalApp)
            .currentObject(savedApp)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedApp);
    }

    @Override
    public boolean existsById(String id) {
        return y9AppRepository.existsById(id);
    }

    @Override
    public Optional<App> findById(String id) {
        return y9AppManager.findByIdFromCache(id).map(y9App -> entityToModel(y9App));
    }

    @Override
    public List<App> findByNameLike(String name) {
        List<Y9App> y9AppList = y9AppRepository.findByNameContainingOrderByTabIndex(name);
        return entityToModel(y9AppList);
    }

    @Override
    public App getById(String id) {
        return entityToModel(y9AppManager.getByIdFromCache(id));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App saveOrUpdate(App app) {
        Y9App y9App = PlatformModelConvertUtil.convert(app, Y9App.class);
        // 每次保存都更改审核状态为未审核
        y9App.setChecked(false);

        if (StringUtils.isNotBlank(y9App.getId())) {
            Optional<Y9App> y9AppOptional = y9AppManager.findById(y9App.getId());
            if (y9AppOptional.isPresent()) {
                Y9App originApp = PlatformModelConvertUtil.convert(y9AppOptional.get(), Y9App.class);
                Y9App savedApp = y9AppManager.update(y9App);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.APP_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.APP_UPDATE.getDescription(), savedApp.getName()))
                    .objectId(savedApp.getId())
                    .oldObject(originApp)
                    .currentObject(savedApp)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return entityToModel(savedApp);
            }
        }

        Y9App savedApp = y9AppManager.insert(y9App);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.APP_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.APP_CREATE.getDescription(), savedApp.getName()))
            .objectId(savedApp.getId())
            .oldObject(null)
            .currentObject(savedApp)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedApp);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public App updateTabIndex(String id, int index) {
        return entityToModel(y9AppManager.updateTabIndex(id, index));
    }
}
