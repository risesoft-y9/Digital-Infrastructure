package net.risesoft.y9public.service.resource.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.specification.Y9AppSpecification;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class Y9AppServiceImpl implements Y9AppService {

    protected final Y9TenantAppManager y9TenantAppManager;
    private final Y9AppManager y9AppManager;
    private final Y9TenantSystemManager y9TenantSystemManager;

    private final Y9AppRepository y9AppRepository;
    private final Y9SystemRepository y9SystemRepository;

    @Override
    public long countBySystemId(String systemId) {
        return y9AppRepository.countBySystemId(systemId);
    }

    @Override
    @Transactional(readOnly = false)
    public void disableBySystemId(String systemId) {
        List<Y9App> list = y9AppRepository.findBySystemId(systemId);
        for (Y9App y9App : list) {
            boolean appEnabled = y9App.getEnabled();
            if (appEnabled) {
                y9App.setEnabled(false);
                y9AppManager.save(y9App);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void enableBySystemId(String systemId) {
        List<Y9App> list = y9AppRepository.findBySystemId(systemId);
        for (Y9App y9App : list) {
            boolean appEnabled = y9App.getEnabled();
            if (!appEnabled) {
                y9App.setEnabled(true);
                y9AppManager.save(y9App);
            }
        }
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
    public Optional<Y9App> findBySystemIdAndCustomId(String systemId, String customId) {
        return y9AppRepository.findBySystemIdAndCustomId(systemId, customId);
    }

    @Override
    public Optional<Y9App> findBySystemNameAndCustomId(String systemName, String customId) {
        Optional<Y9System> y9SystemOptional = y9SystemRepository.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            return y9AppRepository.findBySystemIdAndCustomId(y9SystemOptional.get().getId(), customId);
        }
        return Optional.empty();
    }

    @Override
    public List<Y9App> findByUrlLike(String url) {
        return y9AppRepository.findByUrlContaining(url);
    }

    @Override
    public List<Y9App> listAll() {
        return y9AppRepository.findAll(Sort.by("systemId", "tabIndex"));
    }

    @Override
    public List<Y9App> listByEnable() {
        return y9AppRepository.findByEnabledOrderByTabIndex(true);
    }

    @Override
    public List<Y9App> listByIds(List<String> appIdList) {
        return y9AppRepository.findAllById(appIdList);
    }

    @Override
    public List<Y9App> listByAppName(String appName) {
        if (appName != null && appName.length() > 0) {
            return y9AppRepository.findByName(appName);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Y9App> listByAutoInitAndChecked(Boolean autoInit, Boolean checked) {
        return y9AppRepository.findByAutoInitAndCheckedOrderByCreateTime(autoInit, checked);
    }

    @Override
    public List<Y9App> listByChecked(boolean checked) {
        return y9AppRepository.findByCheckedOrderByCreateTime(checked);
    }

    @Override
    public List<Y9App> listByCustomId(String customId) {
        return y9AppRepository.findByCustomId(customId);
    }

    @Override
    public List<Y9App> listBySystemId(String systemId) {
        return y9AppRepository.findBySystemIdOrderByTabIndex(systemId);
    }

    @Override
    public List<Y9App> listBySystemName(String systemName) {
        Optional<Y9System> y9SystemOptional = y9SystemRepository.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            return y9AppRepository.findBySystemId(y9SystemOptional.get().getId());
        }
        return new ArrayList<>();
    }

    @Override
    public Page<Y9App> page(Y9PageQuery pageQuery, String systemId, String name) {
        Y9AppSpecification<Y9App> specification = new Y9AppSpecification<>(systemId, name);
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(),
            Sort.by(Sort.Direction.ASC, "tabIndex").and(Sort.by(Sort.Direction.DESC, "createTime")));
        return y9AppRepository.findAll(specification, pageRequest);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App saveAndRegister4Tenant(Y9App y9App) {
        Y9App savedApp = this.saveOrUpdate(y9App);
        // 审核应用
        this.verifyApp(y9App.getId(), true, Y9LoginUserHolder.getUserInfo().getName());
        // 租用系统
        y9TenantSystemManager.saveTenantSystem(savedApp.getSystemId(), Y9LoginUserHolder.getTenantId());
        // 租用应用
        y9TenantAppManager.save(savedApp.getId(), Y9LoginUserHolder.getTenantId(), "系统默认租用");
        return savedApp;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App saveIsvApp(Y9App app) {
        if (app.getTabIndex() == null || app.getTabIndex() == 0) {
            Integer tabIndex =
                y9AppRepository.findTopByOrderByTabIndexDesc().map(y9App -> y9App.getTabIndex() + 1).orElse(1);
            app.setTabIndex(tabIndex);
        }

        return saveOrUpdate(app);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(String[] appIds) {
        if (appIds.length > 0) {
            for (int i = 0, len = appIds.length; i < len; i++) {
                Y9App app = getById(appIds[i]);
                app.setTabIndex(i + 1);
                y9AppManager.save(app);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App verifyApp(String id, boolean checked, String verifyUserName) {
        Y9App y9App = this.getById(id);
        y9App.setChecked(checked);
        y9App.setVerifyUserName(verifyUserName);
        return y9AppManager.save(y9App);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> idList) {
        for (String id : idList) {
            this.delete(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9AppManager.delete(id);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9App> disable(List<String> idList) {
        List<Y9App> y9AppList = new ArrayList<>();
        for (String id : idList) {
            y9AppList.add(this.disable(id));
        }
        return y9AppList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App disable(String id) {
        Y9App y9App = this.getById(id);
        y9App.setEnabled(Boolean.FALSE);
        return this.saveOrUpdate(y9App);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9App> enable(List<String> idList) {
        List<Y9App> y9AppList = new ArrayList<>();
        for (String id : idList) {
            y9AppList.add(this.enable(id));
        }
        return y9AppList;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App enable(String id) {
        Y9App y9App = this.getById(id);
        y9App.setEnabled(Boolean.TRUE);
        return this.saveOrUpdate(y9App);
    }

    @Override
    public boolean existsById(String id) {
        return y9AppRepository.existsById(id);
    }

    @Override
    public Optional<Y9App> findById(String id) {
        return y9AppManager.findById(id);
    }

    @Override
    public List<Y9App> findByNameLike(String name) {
        return y9AppRepository.findByNameContainingOrderByTabIndex(name);
    }

    @Override
    public Y9App getById(String id) {
        return y9AppManager.getById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9App saveOrUpdate(Y9App y9App) {
        // 每次保存都更改审核状态为未审核
        y9App.setChecked(false);
        if (StringUtils.isNotBlank(y9App.getId())) {
            Optional<Y9App> y9AppOptional = y9AppManager.findById(y9App.getId());
            if (y9AppOptional.isPresent()) {
                Y9App originY9AppResource = y9AppOptional.get();
                Y9App updatedY9AppResource = new Y9App();
                Y9BeanUtil.copyProperties(originY9AppResource, updatedY9AppResource);
                Y9BeanUtil.copyProperties(y9App, updatedY9AppResource);
                updatedY9AppResource = y9AppManager.save(updatedY9AppResource);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originY9AppResource, updatedY9AppResource));

                return updatedY9AppResource;
            }
        } else {
            y9App.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9App = y9AppManager.save(y9App);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9App));

        return y9App;
    }

    @Override
    public Y9App updateTabIndex(String id, int index) {
        return y9AppManager.updateTabIndex(id, index);
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onTenantAppDeleted(Y9EntityDeletedEvent<Y9TenantApp> event) {
        Y9TenantApp entity = event.getEntity();
        Y9LoginUserHolder.setTenantId(entity.getTenantId());
        y9AppManager.deleteTenantRelatedByAppId(entity.getAppId());
    }

}
