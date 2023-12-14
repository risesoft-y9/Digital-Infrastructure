package net.risesoft.y9public.service.tenant.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.exception.TenantErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
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

    private final Y9AppManager y9AppManager;
    private final Y9TenantAppManager y9TenantAppManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9TenantSystemManager y9TenantSystemManager;

    @Override
    public long countByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantAppRepository.countByTenantIdAndSystemId(tenantId, systemId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByAppId(String appId) {
        y9TenantAppManager.deleteByAppId(appId);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByTenantIdAndAppId(String tenantId, String appId) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByTenantIdAndAppId(tenantId, appId);
        for (Y9TenantApp ta : y9TenantAppList) {
            y9TenantAppManager.delete(ta);
        }
    }

    @Override
    @Transactional(readOnly = false)
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
    public List<String> listAppIdByTenantId(String tenantId, Boolean verify, Boolean tenancy) {
        List<Y9TenantApp> tas =
            y9TenantAppRepository.findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(tenantId, verify, tenancy);
        if (tas != null) {
            return tas.stream().map(Y9TenantApp::getAppId).collect(Collectors.toList());
        }
        return new ArrayList<>();
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
    public List<Y9TenantApp> listByAppIdAndTenancy(String appId, Boolean tenancy) {
        return y9TenantAppManager.listByAppIdAndTenancy(appId, tenancy);
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
    @Transactional(readOnly = false)
    public Y9TenantApp save(String appId, String tenantId, String applyReason) {
        Optional<Y9TenantApp> y9TenantAppOptional =
            y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, Boolean.TRUE);
        Y9App y9App = y9AppManager.getById(appId);
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
        ta.setApplyName(Optional.ofNullable(Y9LoginUserHolder.getUserInfo()).map(UserInfo::getName)
            .orElse(ManagerLevelEnum.SYSTEM_MANAGER.getName()));
        ta.setApplyId(Y9LoginUserHolder.getPersonId());
        ta.setApplyReason(applyReason);
        ta.setTenancy(Boolean.TRUE);
        // 审核状态
        if (StringUtils.isNotBlank(tenantDataSource)) {
            ta.setVerify(Boolean.TRUE);
            ta.setVerifyUserName(Optional.ofNullable(Y9LoginUserHolder.getUserInfo()).map(UserInfo::getName)
                .orElse(ManagerLevelEnum.SYSTEM_MANAGER.getName()));
            ta.setVerifyTime(time);
            ta.setReason("同意申请");
        } else {
            ta.setVerify(Boolean.FALSE);
        }
        return save(ta);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TenantApp save(Y9TenantApp y9TenantApp) {
        if (StringUtils.isBlank(y9TenantApp.getId())) {
            y9TenantApp.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        return y9TenantAppRepository.save(y9TenantApp);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrUpdate(String appId, String appName) {
        List<Y9TenantApp> list = y9TenantAppRepository.findByAppId(appId);
        for (Y9TenantApp ta : list) {
            ta.setApplyName(appName);
            y9TenantAppRepository.save(ta);
        }
    }

    @Override
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App y9App = event.getEntity();
        this.deleteByAppId(y9App.getAppId());
    }
}
