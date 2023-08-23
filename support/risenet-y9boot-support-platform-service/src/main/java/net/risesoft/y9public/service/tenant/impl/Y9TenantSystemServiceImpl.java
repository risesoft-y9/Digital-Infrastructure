package net.risesoft.y9public.service.tenant.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.TenantSystem;
import net.risesoft.y9.pubsub.Y9PublishService;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.message.Y9MessageCommon;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9DataSource;
import net.risesoft.y9public.entity.tenant.Y9Tenant;
import net.risesoft.y9public.entity.tenant.Y9TenantSystem;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.tenant.Y9DataSourceManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;
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
    private final Y9SystemRepository y9SystemRepository;

    private final Y9SystemManager y9SystemManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9TenantSystemManager y9TenantSystemManager;
    private final Y9DataSourceManager y9DataSourceManager;

    private final Y9PublishService y9PublishService;

    @Override
    public long countBySystemId(String systemId) {
        return y9TenantSystemRepository.countBySystemId(systemId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return y9TenantSystemRepository.countByTenantId(tenantId);
    }

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
            delete(t.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteByTenantIdAndSystemId(String tenantId, String systemId) {
        Y9TenantSystem y9TenantSystem = y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
        if (y9TenantSystem != null) {
            y9TenantSystemRepository.delete(y9TenantSystem);
        }
    }

    @Override
    public long existByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantSystemRepository.countByTenantIdAndSystemId(tenantId, systemId);
    }

    @Override
    public Y9TenantSystem findById(String id) {
        return y9TenantSystemRepository.findById(id).orElse(null);
    }

    @Override
    public Y9TenantSystem getByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
    }

    @Override
    public String getDataSourceIdByTenantIdAndSystemId(String tenantId, String systemId) {
        return y9TenantSystemManager.getDataSourceIdByTenantIdAndSystemId(tenantId, systemId);
    }

    @Override
    public String getIdByTenantIdAndSystemId(String tenantId, String systemId) {
        Y9TenantSystem y9TenantSystem = y9TenantSystemRepository.findByTenantIdAndSystemId(tenantId, systemId);
        if (null != y9TenantSystem) {
            return y9TenantSystem.getId();
        }
        return null;
    }

    @Override
    public List<Y9TenantSystem> listBySystemId(String systemId) {
        return y9TenantSystemRepository.findBySystemId(systemId);
    }

    @Override
    public List<Y9TenantSystem> listByTenantDataSource(String tenantDataSource) {
        return y9TenantSystemRepository.findByTenantDataSource(tenantDataSource);
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
    public Page<Y9TenantSystem> pageByTenantId(int page, int rows, String tenantId) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows);
        return y9TenantSystemRepository.findByTenantId(tenantId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(Y9TenantSystem entity) {
        if (entity != null) {
            // 当moduleTree.getGuid()为空或者null时，为新增
            if (StringUtils.isBlank(entity.getId())) {
                // 设置guid
                entity.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            y9TenantSystemRepository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveAndInitializedTenantSystem(Y9TenantSystem y9TenantSystem, Y9System y9System,
        String tenantId) {
        Map<String, Object> map = new HashMap<>(8);
        String systemId = y9TenantSystem.getSystemId();
        long count = this.existByTenantIdAndSystemId(tenantId, systemId);
        if (count > 0) {
            map.put("success", true);
            map.put("msg", y9System.getCnName() + "已租用!");
            return map;
        }
        map.put("success", false);
        map.put("systemId", systemId);
        map.put("dataSourceId", y9TenantSystem.getTenantDataSource());
        map.put("msg", y9System.getCnName() + "初始化数据库失败!该系统不存在全量sql文件，初始化表结构失败，如果需要初始化表结构，请编辑该租户系统，手动触发租户库初始化表结构!");
        y9TenantSystem.setInitialized(false);
        y9TenantSystem.setTenantId(tenantId);
        this.save(y9TenantSystem);

        // 租户租用系统事件，应用可监听做对应租户的初始化的工作
        Y9MessageCommon event = new Y9MessageCommon();
        event.setEventObject(Y9ModelConvertUtil.convert(y9TenantSystem, TenantSystem.class));
        event.setTarget(y9System.getName());
        event.setEventType(Y9CommonEventConst.TENANT_SYSTEM_REGISTERED);
        y9PublishService.publishMessageCommon(event);
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveTenantSystem(String systemId, String tenantId) {
        Y9Tenant tenant = y9TenantManager.getById(tenantId);
        Map<String, Object> map;
        Y9System y9System = y9SystemRepository.findById(systemId).orElse(null);
        Y9TenantSystem y9TenantSystem = new Y9TenantSystem();
        y9TenantSystem.setTenantId(tenantId);
        y9TenantSystem.setTenantDataSource(tenant.getDefaultDataSourceId());
        y9TenantSystem.setSystemId(systemId);
        if (y9System != null && Boolean.TRUE.equals(y9System.getSingleDatasource())) {
            String datasoureId = tenant.getDefaultDataSourceId();
            try {
                Y9DataSource y9DataSource = y9DataSourceManager.createTenantDefaultDataSource(tenant.getShortName(),
                    tenant.getTenantType(), y9System.getName());
                datasoureId = y9DataSource.getId();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
            y9TenantSystem.setTenantDataSource(datasoureId);
        }
        return this.saveAndInitializedTenantSystem(y9TenantSystem, y9System, tenantId);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Map<String, Object>> saveTenantSystems(String[] systemIds, String tenantId) throws Exception {
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (String systemId : systemIds) {
            Map<String, Object> map = saveTenantSystem(systemId, tenantId);
            msgList.add(map);
        }
        return msgList;
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
        Y9System y9System = y9SystemRepository.findByName(systemName);
        if (y9System != null) {
            return this.listTenantBySystemId(y9System.getId());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Y9System> listSystemByTenantId(String tenantId) {
        List<String> systemIdList = this.listSystemIdByTenantId(tenantId);
        List<Y9System> y9SystemList = new ArrayList<>();
        if (!systemIdList.isEmpty()) {
            for (String systemId : systemIdList) {
                Y9System y9System = y9SystemRepository.findById(systemId).orElse(null);
                if (y9System != null) {
                    y9SystemList.add(y9System);
                }
            }
        }
        return y9SystemList;
    }

    @Override
    @Transactional(readOnly = false)
    public void registerTenantSystem(String tenantId, String systemId) {
        Y9TenantSystem y9TenantSystem = this.getByTenantIdAndSystemId(tenantId, systemId);
        if (y9TenantSystem == null) {
            this.saveTenantSystem(systemId, tenantId);

            Y9System y9System = y9SystemManager.getById(systemId);

            // 发送同步数据源的消息
            Y9MessageCommon event =
                new Y9MessageCommon(y9System.getName(), null, Y9CommonEventConst.TENANT_DATASOURCE_SYNC);
            y9PublishService.publishMessageCommon(event);
        }
    }
}
