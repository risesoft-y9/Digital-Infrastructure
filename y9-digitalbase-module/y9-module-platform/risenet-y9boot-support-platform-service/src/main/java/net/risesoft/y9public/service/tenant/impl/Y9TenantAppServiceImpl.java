package net.risesoft.y9public.service.tenant.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.ArrayList;
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
import net.risesoft.model.platform.tenant.TenantApp;
import net.risesoft.pojo.Y9Page;
import net.risesoft.query.platform.TenantAppQuery;
import net.risesoft.util.PlatformModelConvertUtil;
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

    private static List<TenantApp> entityToModel(List<Y9TenantApp> y9TenantAppList) {
        return PlatformModelConvertUtil.convert(y9TenantAppList, TenantApp.class);
    }

    private static TenantApp entityToModel(Y9TenantApp y9TenantApp) {
        return PlatformModelConvertUtil.convert(y9TenantApp, TenantApp.class);
    }

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
    public Optional<TenantApp> findById(String id) {
        return y9TenantAppRepository.findById(id).map(Y9TenantAppServiceImpl::entityToModel);
    }

    @Override
    public TenantApp getById(String id) {
        return y9TenantAppRepository.findById(id)
            .map(Y9TenantAppServiceImpl::entityToModel)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(TenantErrorCodeEnum.TENANT_APP_NOT_FOUND, id));
    }

    @Override
    public Optional<TenantApp> findByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy) {
        return y9TenantAppManager.getByTenantIdAndAppIdAndTenancy(tenantId, appId, tenancy)
            .map(Y9TenantAppServiceImpl::entityToModel);
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
    public Y9Page<TenantApp> page(Integer page, Integer rows, TenantAppQuery tenantAppQuery) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime").and(Sort.by(Sort.Direction.ASC, "verify"));
        Y9TenantAppSpecification spec = new Y9TenantAppSpecification(tenantAppQuery);
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Y9TenantApp> y9TenantAppPage = y9TenantAppRepository.findAll(spec, pageable);
        return Y9Page.success(page, y9TenantAppPage.getTotalPages(), y9TenantAppPage.getTotalElements(),
            entityToModel(y9TenantAppPage.getContent()));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public TenantApp save(String appId, String tenantId, String applyReason) {
        return entityToModel(y9TenantAppManager.save(appId, tenantId, applyReason));
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public TenantApp save(TenantApp tenantApp) {
        Y9TenantApp y9TenantApp = PlatformModelConvertUtil.convert(tenantApp, Y9TenantApp.class);
        return entityToModel(y9TenantAppManager.save(y9TenantApp));
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

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App y9App = event.getEntity();
        this.deleteByAppId(y9App.getId());
    }
}
