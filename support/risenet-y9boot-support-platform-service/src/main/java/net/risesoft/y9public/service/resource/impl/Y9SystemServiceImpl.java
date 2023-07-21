package net.risesoft.y9public.service.resource.impl;

import java.util.ArrayList;
import java.util.List;
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
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.specification.Y9SystemSpecification;

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

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9AppManager.deleteBySystemId(id);
        y9TenantSystemManager.deleteBySystemId(id);
        y9SystemRepository.deleteById(id);
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
        return y9SystemRepository.save(systemEntity);
    }

    @Override
    public Y9System findByCnName(String cnName) {
        return y9SystemRepository.findByCnName(cnName);
    }

    @Override
    public Y9System findById(String id) {
        return y9SystemRepository.findById(id).orElse(null);
    }

    @Override
    public Y9System findByName(String name) {
        return y9SystemRepository.findByName(name);
    }

    @Override
    public Y9System findBySystemCnName(String cnName) {
        return y9SystemRepository.findByCnName(cnName);
    }

    @Override
    public Y9System getById(String id) {
        return y9SystemRepository.findById(id).orElseThrow(() -> Y9ExceptionUtil.notFoundException(SystemErrorCodeEnum.SYSTEM_NOT_FOUND, id));
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
    public List<Y9System> listByIsvGuid(String isvGuid) {
        return y9SystemRepository.findByIsvGuidOrderByTabIndexAsc(isvGuid);
    }

    @Override
    public List<String> listSystemNameByIds(List<String> ids) {
        List<String> systemNameList = new ArrayList<>();
        if (!ids.isEmpty()) {
            for (String id : ids) {
                Y9System se = y9SystemRepository.findById(id).orElse(null);
                String systemName = se != null ? se.getName() : "";
                if (!"riseplatform".equals(systemName)) {
                    systemNameList.add(systemName);
                }
            }
        }
        return systemNameList;
    }

    /* (non-Javadoc)
     * @see net.risesoft.y9public.service.resource.Y9SystemService#pageByAll(int, int)
     */
    @Override
    public Page<Y9System> page(int page, int rows) {
        Pageable pageable = PageRequest.of(page < 1 ? 0 : page - 1, rows, Sort.by(Sort.Direction.ASC, "tabIndex"));
        return y9SystemRepository.findAll(pageable);
    }

    @Override
    public Page<Y9System> page(int page, int rows, String isvGuid, String name) {
        Pageable pageable = PageRequest.of(page < 1 ? 0 : page - 1, rows, Sort.by(Sort.Direction.ASC, "tabIndex"));
        Y9SystemSpecification<Y9System> spec = new Y9SystemSpecification<>(isvGuid, name);
        return y9SystemRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(String[] systemIds) {
        if (systemIds.length > 0) {
            for (int i = 0, len = systemIds.length; i < len; i++) {
                Y9System system = getById(systemIds[i]);
                system.setTabIndex(i + 1);
                y9SystemRepository.save(system);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9System saveOrUpdate(Y9System y9System) {
        if (StringUtils.isNotBlank(y9System.getId())) {
            Y9System oldY9System = y9SystemRepository.findById(y9System.getId()).orElse(null);
            if (oldY9System != null) {
                Y9BeanUtil.copyProperties(y9System, oldY9System);
                return y9SystemRepository.save(oldY9System);
            }
        } else {
            y9System.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            if (y9System.getTabIndex() == null) {
                Y9System system = y9SystemRepository.findTopByOrderByTabIndexDesc();
                Integer maxIndex = system != null ? system.getTabIndex() + 1 : 0;
                y9System.setTabIndex(maxIndex);
            }
        }
        return y9SystemRepository.save(y9System);
    }

}
