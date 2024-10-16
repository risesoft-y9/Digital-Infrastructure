package net.risesoft.y9public.service.tenant.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.tenant.Y9TenantSystemRepository;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * @author dingzhaojun
 * @author mengjuhua
 * @author qinman
 * @author shidaobang
 */
@Service(value = "tenantSystemService")
@Slf4j
@RequiredArgsConstructor
public class Y9TenantSystemServiceImpl implements Y9TenantSystemService {

    private final Y9TenantSystemRepository y9TenantSystemRepository;

    private final Y9SystemManager y9SystemManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9TenantSystemManager y9TenantSystemManager;

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9TenantSystemManager.delete(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByTenantId(String tenantId) {
        List<Y9TenantSystem> y9TenantSystemList = y9TenantSystemRepository.findByTenantId(tenantId);
        for (Y9TenantSystem t : y9TenantSystemList) {
            this.delete(t.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByTenantIdAndSystemId(String tenantId, String systemId) {
        Optional<Y9TenantSystem> systemOptional =
            y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
        if (systemOptional.isPresent()) {
            this.delete(systemOptional.get().getId());
        }
    }

    @Override
    public Optional<Y9TenantSystem> findById(String id) {
        return y9TenantSystemRepository.findById(id);
    }

    @Override
    public Optional<Y9TenantSystem> getByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
    }

    @Override
    public List<Y9TenantSystem> listBySystemId(String systemId) {
        return y9TenantSystemRepository.findBySystemId(systemId);
    }

    @Override
    public List<String> listSystemIdByTenantId(String tenantId) {
        List<Y9TenantSystem> ts = y9TenantSystemRepository.findByTenantId(tenantId);
        if (ts != null) {
            return ts.stream().map(Y9TenantSystem::getSystemId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> listTenantIdBySystemId(String systemId) {
        List<Y9TenantSystem> ts = y9TenantSystemRepository.findBySystemId(systemId);
        if (ts != null) {
            return ts.stream().map(Y9TenantSystem::getTenantId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Page<Y9TenantSystem> pageByTenantId(String tenantId, Y9PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.getPage4Db(), pageQuery.getSize());
        return y9TenantSystemRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TenantSystem save(Y9TenantSystem y9TenantSystem) {
        return y9TenantSystemManager.save(y9TenantSystem);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TenantSystem saveTenantSystem(String systemId, String tenantId) {
        return y9TenantSystemManager.saveTenantSystem(systemId, tenantId);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Y9TenantSystem> saveTenantSystems(String[] systemIds, String tenantId) {
        List<Y9TenantSystem> y9TenantSystemList = new ArrayList<>();
        for (String systemId : systemIds) {
            y9TenantSystemList.add(saveTenantSystem(systemId, tenantId));
        }
        return y9TenantSystemList;
    }

    @Override
    public List<Y9Tenant> listTenantBySystemId(String systemId) {
        List<String> tenantIdList = this.listTenantIdBySystemId(systemId);
        List<Y9Tenant> y9TenantList = new ArrayList<>();
        if (!tenantIdList.isEmpty()) {
            for (String tenantId : tenantIdList) {
                Y9Tenant tenant = y9TenantManager.getById(tenantId);
                if (tenant != null) {
                    y9TenantList.add(tenant);
                }
            }
        }
        return y9TenantList;
    }

    @Override
    public List<Y9Tenant> listTenantBySystemName(String systemName) {
        Optional<Y9System> y9SystemOptional = y9SystemManager.findByName(systemName);
        if (y9SystemOptional.isPresent()) {
            return this.listTenantBySystemId(y9SystemOptional.get().getId());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Y9System> listSystemByTenantId(String tenantId) {
        List<String> systemIdList = this.listSystemIdByTenantId(tenantId);
        List<Y9System> y9SystemList = new ArrayList<>();
        if (!systemIdList.isEmpty()) {
            for (String systemId : systemIdList) {
                y9SystemManager.findById(systemId).ifPresent(y9SystemList::add);
            }
        }
        return y9SystemList;
    }
}
