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

import net.risesoft.exception.SystemErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9Assert;
import net.risesoft.y9.util.Y9BeanUtil;
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
        y9AppManager.deleteBySystemId(id);
        y9TenantSystemManager.deleteBySystemId(id);
        y9SystemManager.delete(id);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System disable(String id) {
        Y9System y9System = this.getById(id);
        y9System.setEnabled(Boolean.FALSE);
        // TODO 禁用系统同时禁用应用
        // appService.disableAppBySystemId(id);
        return this.saveOrUpdate(y9System);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System enable(String id) {
        Y9System systemEntity = this.getById(id);
        systemEntity.setEnabled(Boolean.TRUE);
        // TODO 启用系统同时启用应用？
        // appService.enableAppBySystemId(id);
        return y9SystemManager.save(systemEntity);
    }

    @Override
    public Optional<Y9System> findById(String id) {
        return y9SystemManager.findById(id);
    }

    @Override
    public Optional<Y9System> findByName(String name) {
        return y9SystemRepository.findByName(name);
    }

    @Override
    public Y9System getById(String id) {
        return y9SystemManager.getById(id);
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
    public Page<Y9System> page(Y9PageQuery pageQuery) {
        Pageable pageable =
            PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize(), Sort.by(Sort.Direction.ASC, "tabIndex"));
        return y9SystemRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(String[] systemIds) {
        if (systemIds.length > 0) {
            for (int i = 0, len = systemIds.length; i < len; i++) {
                Y9System system = getById(systemIds[i]);
                system.setTabIndex(i + 1);
                y9SystemManager.save(system);
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
                Y9System oldY9System = y9SystemOptional.get();
                Y9BeanUtil.copyProperties(y9System, oldY9System);
                return y9SystemManager.save(oldY9System);
            }
        } else {
            y9System.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            if (y9System.getTabIndex() == null) {
                Integer maxIndex =
                    y9SystemRepository.findTopByOrderByTabIndexDesc().map(system -> system.getTabIndex() + 1).orElse(0);
                y9System.setTabIndex(maxIndex);
            }
        }
        return y9SystemManager.save(y9System);
    }

    @Override
    public boolean isNameAvailable(String id, String name) {
        Optional<Y9System> y9SystemOptional = y9SystemRepository.findByName(name);
        if (y9SystemOptional.isEmpty()) {
            return true;
        }
        // 修改系统时的检查也为可用
        return y9SystemOptional.get().getId().equals(id);
    }

}
