package net.risesoft.y9public.service.tenant.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.repository.tenant.Y9TenantAppRepository;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;
import net.risesoft.y9public.specification.Y9TenantAppSpecification;

/**
 * @author dingzhaojun
 * @author mengjuhua
 * @author qinman
 * @author shidaobang
 */
@Service(value = "tenantAppService")
@Slf4j
@RequiredArgsConstructor
public class Y9TenantAppServiceImpl implements Y9TenantAppService {

    private final Y9TenantAppRepository y9TenantAppRepository;

    private final Y9TenantAppManager y9TenantAppManager;

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void deleteByAppId(String appId) {
        y9TenantAppManager.deleteByAppId(appId);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void deleteByTenantIdAndAppId(String tenantId, String appId) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByTenantIdAndAppId(tenantId, appId);
        for (Y9TenantApp ta : y9TenantAppList) {
            y9TenantAppManager.delete(ta);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void deleteByTenantIdAndSystemId(String tenantId, String systemId) {
        List<Y9TenantApp> list = y9TenantAppRepository.findByTenantIdAndSystemId(tenantId, systemId);
        for (Y9TenantApp y9TenantApp : list) {
            y9TenantAppManager.delete(y9TenantApp);
        }
    }

    @Override
    public Optional<Y9TenantApp> findById(String id) {
        return y9TenantAppRepository.findById(id);
    }

    @Override
    public Y9TenantApp getById(String id) {
        return y9TenantAppRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(TenantErrorCodeEnum.TENANT_APP_NOT_FOUND, id));
    }

    @Override
    public Optional<Y9TenantApp> getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy) {
        return y9TenantAppManager.getByTenantIdAndAppIdAndTenancy(tenantId, appId, tenancy);
    }

    @Override
    public List<String> listAppIdBySystemIdAndTenantId(String systemId, String tenantId, Boolean verify,
        Boolean tenancy) {
        List<Y9TenantApp> tas =
            y9TenantAppRepository.findByTenantIdAndSystemIdAndVerifyAndTenancy(tenantId, systemId, verify, tenancy);
        return tas.stream().map(Y9TenantApp::getAppId).collect(Collectors.toList());
    }

    @Override
    public List<String> listAppIdByTenantId(String tenantId, Boolean verify, Boolean tenancy) {
        List<Y9TenantApp> tas =
            y9TenantAppRepository.findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(tenantId, verify, tenancy);
        return tas.stream().map(Y9TenantApp::getAppId).collect(Collectors.toList());
    }

    @Override
    public List<String> listAppIdByTenantIdAndTenancy(String tenantId, Boolean tenancy) {
        List<Y9TenantApp> tas = y9TenantAppRepository.findByTenantIdAndTenancy(tenantId, tenancy);
        if (tas != null) {
            return tas.stream().map(Y9TenantApp::getAppId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Y9TenantApp> listByTenantIdAndTenancy(String tenantId, Boolean verify, Boolean tenancy) {
        return y9TenantAppRepository.findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(tenantId, verify, tenancy);
    }

    @Override
    public Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime,
        String verifyTime, Boolean tenancy, String systemId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "verify").and(Sort.by(Sort.Direction.DESC, "createTime"));
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Y9TenantAppSpecification<Y9TenantApp> spec =
            new Y9TenantAppSpecification<>(verify, tenantName, createTime, verifyTime, tenancy, systemId);
        return y9TenantAppRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime,
        String verifyTime, Boolean tenancy, String appName, String systemIds) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime").and(Sort.by(Sort.Direction.ASC, "verify"));
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Y9TenantAppSpecification<Y9TenantApp> spec =
            new Y9TenantAppSpecification<>(verify, tenantName, createTime, verifyTime, tenancy, systemIds, appName);
        return y9TenantAppRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9TenantApp save(String appId, String tenantId, String applyReason) {
        return y9TenantAppManager.save(appId, tenantId, applyReason);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9TenantApp save(Y9TenantApp y9TenantApp) {
        return y9TenantAppManager.save(y9TenantApp);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void saveOrUpdate(String appId, String appName) {
        List<Y9TenantApp> list = y9TenantAppRepository.findByAppId(appId);
        for (Y9TenantApp ta : list) {
            ta.setApplyName(appName);
            y9TenantAppRepository.save(ta);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public int updateByAppIdAndTenantId(Boolean tenancy, String deletedName, Date deletedTime, String appId,
        String tenantId, Boolean currentTenancy) {
        try {
            Optional<Y9TenantApp> y9TenantAppOptional =
                y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, currentTenancy);
            if (y9TenantAppOptional.isPresent()) {
                Y9TenantApp ta = y9TenantAppOptional.get();
                ta.setTenancy(tenancy);
                ta.setDeletedName(deletedName);
                ta.setDeletedTime(deletedTime);
                y9TenantAppRepository.save(ta);
            }
            return 1;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return 0;
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Y9TenantApp verify(Y9TenantApp y9TenantApp, String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        if (null != userInfo) {
            y9TenantApp.setVerifyUserName(userInfo.getName());
        } else {
            y9TenantApp.setVerifyUserName("systemAdmin");
        }
        y9TenantApp.setVerify(Boolean.TRUE);
        y9TenantApp.setVerifyTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        y9TenantApp.setReason(reason);
        return save(y9TenantApp);
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App y9App = event.getEntity();
        this.deleteByAppId(y9App.getAppId());
    }
}
