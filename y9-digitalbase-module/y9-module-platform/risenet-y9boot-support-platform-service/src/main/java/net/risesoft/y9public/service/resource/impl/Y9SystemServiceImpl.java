package net.risesoft.y9public.service.resource.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.AuditLogEnum;
import net.risesoft.exception.SystemErrorCodeEnum;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;
import net.risesoft.y9public.service.resource.Y9SystemService;

/**
 * SystemServiceImpl
 *
 * @author shidaobang
 * @author mengjuhua
 * @date 2022/3/2
 */
@Service
@RequiredArgsConstructor
public class Y9SystemServiceImpl implements Y9SystemService {

    private final Y9SystemRepository y9SystemRepository;

    private final Y9AppManager y9AppManager;
    private final Y9TenantSystemManager y9TenantSystemManager;
    private final Y9SystemManager y9SystemManager;

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9System y9System = y9SystemManager.getById(id);
        y9AppManager.deleteBySystemId(id);
        y9TenantSystemManager.deleteBySystemId(id);
        y9SystemManager.delete(id);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.SYSTEM_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.SYSTEM_DELETE.getDescription(), y9System.getName()))
            .objectId(id)
            .oldObject(y9System)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System disable(String id) {
        Y9System currentSystem = y9SystemManager.getById(id);
        Y9System originalSystem = Y9ModelConvertUtil.convert(currentSystem, Y9System.class);

        currentSystem.setEnabled(Boolean.FALSE);
        // TODO 禁用系统同时禁用应用
        // appService.disableAppBySystemId(id);
        Y9System savedSystem = y9SystemManager.update(currentSystem);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.SYSTEM_UPDATE_ENABLE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.SYSTEM_UPDATE_ENABLE.getDescription(), savedSystem.getName(), "禁用"))
            .objectId(id)
            .oldObject(originalSystem)
            .currentObject(savedSystem)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedSystem;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System enable(String id) {
        Y9System currentSystem = y9SystemManager.getById(id);
        Y9System originalSystem = Y9ModelConvertUtil.convert(currentSystem, Y9System.class);

        currentSystem.setEnabled(Boolean.TRUE);
        // TODO 启用系统同时启用应用？
        // appService.enableAppBySystemId(id);
        Y9System savedSystem = y9SystemManager.update(currentSystem);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.SYSTEM_UPDATE_ENABLE.getAction())
            .description(
                Y9StringUtil.format(AuditLogEnum.SYSTEM_UPDATE_ENABLE.getDescription(), savedSystem.getName(), "启用"))
            .objectId(id)
            .oldObject(originalSystem)
            .currentObject(savedSystem)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedSystem;
    }

    @Override
    public Optional<Y9System> findById(String id) {
        return y9SystemManager.findByIdFromCache(id);
    }

    @Override
    public Optional<Y9System> findByName(String name) {
        return y9SystemRepository.findByName(name);
    }

    @Override
    public Y9System getById(String id) {
        return y9SystemManager.getByIdFromCache(id);
    }

    @Override
    public Y9System getByName(String systemName) {
        return y9SystemRepository.findByName(systemName)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(SystemErrorCodeEnum.SYSTEM_NOT_FOUND, systemName));
    }

    @Override
    public List<Y9System> listAll() {
        return y9SystemRepository.findAll(Sort.by(Sort.Direction.ASC, "tabIndex"));
    }

    @Override
    public List<String> listByAutoInit(Boolean autoInit) {
        List<Y9System> systems = y9SystemRepository.findByAutoInit(autoInit);
        return systems.stream().map(Y9System::getId).collect(Collectors.toList());
    }

    @Override
    public List<Y9System> listByCnNameContaining(String cnName) {
        return y9SystemRepository.findByCnNameContainingOrderByTabIndexAsc(cnName);
    }

    @Override
    public List<Y9System> listByContextPath(String contextPath) {
        return y9SystemRepository.findByContextPath(contextPath);
    }

    @Override
    public List<Y9System> listByIds(List<String> systemIdList) {
        return y9SystemRepository.findAllById(systemIdList);
    }

    @Override
    public List<Y9System> listByTenantId(String tenantId) {
        return y9SystemRepository.findByTenantIdOrderByTabIndexAsc(tenantId);
    }

    @Override
    public Page<Y9System> page(Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "tabIndex"));
        return y9SystemRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System saveAndRegister4Tenant(Y9System y9System) {
        if (Y9LoginUserHolder.getUserInfo().getManagerLevel().isTenantManager()) {
            y9System.setTenantId(Y9LoginUserHolder.getTenantId());
            Y9System savedSystem = this.saveOrUpdate(y9System);
            y9TenantSystemManager.saveTenantSystem(savedSystem.getId(), Y9LoginUserHolder.getTenantId());
            return savedSystem;
        } else {
            y9System.setTenantId(null);
            return this.saveOrUpdate(y9System);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(String[] systemIds) {
        if (systemIds.length > 0) {
            for (int i = 0, len = systemIds.length; i < len; i++) {
                Y9System system = getById(systemIds[i]);
                system.setTabIndex(i + 1);
                y9SystemManager.update(system);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System saveOrUpdate(Y9System y9System) {
        Y9Assert.isTrue(isNameAvailable(y9System.getId(), y9System.getName()),
            SystemErrorCodeEnum.SYSTEM_WITH_SPECIFIC_NAME_EXISTS, y9System.getName());

        if (StringUtils.isNotBlank(y9System.getId())) {
            Optional<Y9System> y9SystemOptional = y9SystemManager.findById(y9System.getId());
            if (y9SystemOptional.isPresent()) {
                Y9System originalSystem = Y9ModelConvertUtil.convert(y9SystemOptional.get(), Y9System.class);
                Y9System savedSystem = y9SystemManager.update(y9System);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.SYSTEM_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.SYSTEM_UPDATE.getDescription(), savedSystem.getName()))
                    .objectId(savedSystem.getId())
                    .oldObject(originalSystem)
                    .currentObject(savedSystem)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedSystem;
            }
        }

        if (y9System.getTabIndex() == null) {
            Integer maxIndex =
                y9SystemRepository.findTopByOrderByTabIndexDesc().map(system -> system.getTabIndex() + 1).orElse(0);
            y9System.setTabIndex(maxIndex);
        }
        Y9System savedSystem = y9SystemManager.insert(y9System);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.SYSTEM_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.SYSTEM_CREATE.getDescription(), savedSystem.getName()))
            .objectId(savedSystem.getId())
            .oldObject(null)
            .currentObject(savedSystem)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedSystem;
    }

    private boolean isNameAvailable(String id, String name) {
        Optional<Y9System> y9SystemOptional = y9SystemRepository.findByName(name);
        if (y9SystemOptional.isEmpty()) {
            return true;
        }
        // 修改系统时的检查也为可用
        return y9SystemOptional.get().getId().equals(id);
    }

}
