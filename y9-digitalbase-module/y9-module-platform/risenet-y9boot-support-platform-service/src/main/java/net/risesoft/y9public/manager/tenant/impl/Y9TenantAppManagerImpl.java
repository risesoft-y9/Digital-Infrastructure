package net.risesoft.y9public.manager.tenant.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.tenant.TenantApp;
import net.risesoft.model.user.UserInfo;
import net.risesoft.util.Y9PublishServiceUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;

/**
 * 租户应用 Manager 实现类
 *
 * @author shidaobang
 * @date 2023/06/14
 * @since 9.6.2
 */
@Service
@RequiredArgsConstructor
public class Y9TenantAppManagerImpl implements Y9TenantAppManager {

    private final Y9TenantAppRepository y9TenantAppRepository;

    private final Y9AppManager y9AppManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9TenantSystemManager y9TenantSystemManager;
    private final Y9SystemManager y9SystemManager;

    @Override
    public void deleteByAppId(String appId) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByAppId(appId);
        for (Y9TenantApp y9TenantApp : y9TenantAppList) {
            delete(y9TenantApp);
        }
    }

    @Override
    public void delete(Y9TenantApp y9TenantApp) {
        y9TenantAppRepository.delete(y9TenantApp);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9TenantApp));
    }

    @Override
    public Optional<Y9TenantApp> getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy) {
        return y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, tenancy);
    }

    @Override
    public List<Y9TenantApp> listByAppIdAndTenancy(String appId, Boolean tenancy) {
        return y9TenantAppRepository.findByAppIdAndTenancy(appId, tenancy);
    }

    @Override
    public Y9TenantApp save(String appId, String tenantId, String applyReason) {
        Optional<Y9TenantApp> y9TenantAppOptional =
            y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, Boolean.TRUE);
        Y9App y9App = y9AppManager.getByIdFromCache(appId);
        String tenantDataSource =
            y9TenantSystemManager.getDataSourceIdByTenantIdAndSystemId(tenantId, y9App.getSystemId());
        if (y9TenantAppOptional.isPresent()) {
            Y9TenantApp y9TenantApp = y9TenantAppOptional.get();
            if (tenantDataSource != null && !y9TenantApp.getVerify()) {
                y9TenantApp.setVerify(true);
                return save(y9TenantApp);
            }
            return y9TenantApp;
        }

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Y9TenantApp ta = new Y9TenantApp();
        ta.setId(Y9IdGenerator.genId());
        ta.setTenantId(tenantId);
        ta.setTenantName(y9TenantManager.getById(tenantId).getName());
        ta.setAppId(appId);
        ta.setSystemId(y9App.getSystemId());
        ta.setAppName(y9App.getName());
        ta.setApplyName(Optional.ofNullable(Y9LoginUserHolder.getUserInfo())
            .map(UserInfo::getName)
            .orElse(ManagerLevelEnum.SYSTEM_MANAGER.getName()));
        ta.setApplyId(Y9LoginUserHolder.getPersonId());
        ta.setApplyReason(applyReason);
        ta.setTenancy(Boolean.TRUE);
        // 审核状态
        if (StringUtils.isNotBlank(tenantDataSource)) {
            ta.setVerify(Boolean.TRUE);
            ta.setVerifyUserName(Optional.ofNullable(Y9LoginUserHolder.getUserInfo())
                .map(UserInfo::getName)
                .orElse(ManagerLevelEnum.SYSTEM_MANAGER.getName()));
            ta.setVerifyTime(time);
            ta.setReason("同意申请");
        } else {
            ta.setVerify(Boolean.FALSE);
        }
        return save(ta);
    }

    @Override
    public Y9TenantApp save(Y9TenantApp y9TenantApp) {
        if (StringUtils.isBlank(y9TenantApp.getId())) {
            y9TenantApp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        Y9TenantApp savedTenantApp = y9TenantAppRepository.save(y9TenantApp);

        if (Boolean.TRUE.equals(y9TenantApp.getTenancy())) {
            Y9System y9System = y9SystemManager.getByIdFromCache(savedTenantApp.getSystemId());
            TenantApp tenantApp = Y9ModelConvertUtil.convert(savedTenantApp, TenantApp.class);
            // 注册事务同步器，在事务提交后做某些操作
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // 租户租用系统事件，应用可监听做对应租户的初始化的工作
                        Y9MessageCommon tenantSystemRegisteredEvent = new Y9MessageCommon();
                        tenantSystemRegisteredEvent.setEventObject(tenantApp);
                        tenantSystemRegisteredEvent.setEventTarget(y9System.getName());
                        tenantSystemRegisteredEvent.setEventType(Y9CommonEventConst.TENANT_APP_REGISTERED);
                        Y9PublishServiceUtil.publishMessageCommon(tenantSystemRegisteredEvent);
                    }
                });
            }
        }
        return savedTenantApp;
    }
}
