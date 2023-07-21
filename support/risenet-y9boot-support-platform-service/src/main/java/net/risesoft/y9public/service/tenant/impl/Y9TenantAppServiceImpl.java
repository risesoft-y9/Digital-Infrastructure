package net.risesoft.y9public.service.tenant.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.pubsub.constant.Y9TopicConst;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.tenant.Y9TenantApp;
import net.risesoft.y9public.manager.tenant.Y9TenantAppManager;
import net.risesoft.y9public.manager.tenant.Y9TenantManager;
import net.risesoft.y9public.manager.tenant.Y9TenantSystemManager;
import net.risesoft.y9public.repository.resource.Y9AppRepository;
import net.risesoft.y9public.repository.resource.Y9SystemRepository;
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

    private final KafkaTemplate<String, Object> y9KafkaTemplate;

    private final Y9SystemRepository y9SystemRepository;
    private final Y9TenantAppRepository y9TenantAppRepository;
    private final Y9AppRepository y9AppRepository;

    private final Y9TenantAppManager y9TenantAppManager;
    private final Y9TenantManager y9TenantManager;
    private final Y9TenantSystemManager y9TenantSystemManager;

    private void copyItemData(String tenantId, Y9System system, Y9App y9App, String personId) {
        String appId = y9App.getId();
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByTenantIdAndAppIdAndVerifyTrue(tenantId, appId);
        if (!y9TenantAppList.isEmpty()) {
            return;
        }
        String isvTenantId = system.getIsvGuid();
        String url = y9App.getUrl();
        if (url.contains("itemId")) {
            // 1、获取事项Id
            String itemId = "";
            String parameter = url.split("\\?")[1];
            if (parameter.contains("&")) {
                String[] parameterArray = parameter.split("&");
                for (String pTemp : parameterArray) {
                    if (pTemp.contains("itemId")) {
                        itemId = pTemp.split("=")[1];
                    }
                }
            } else {
                itemId = parameter.split("=")[1];
            }
            if (StringUtils.isEmpty(itemId)) {
                return;
            }
            // 2、租户租用流程管理、事项管理还有改应用对应的系统
            try {
                HashMap<String, Object> map = new HashMap<>(8);
                map.put("sourceTenantId", isvTenantId);
                map.put("targetTenantId", tenantId);
                map.put("itemId", itemId);
                map.put("personId", personId);
                String jsonString = Y9JsonUtil.writeValueAsString(map);
                if (y9KafkaTemplate != null) {
                    y9KafkaTemplate.send(Y9TopicConst.Y9_DATACOPY_MESSAGE, jsonString);
                    LOGGER.info(Y9TopicConst.Y9_DATACOPY_MESSAGE + "   ->" + jsonString);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        } else {
            String systemName = system.getName();
            try {
                HashMap<String, Object> map = new HashMap<>(8);
                map.put("sourceTenantId", isvTenantId);
                map.put("targetTenantId", tenantId);
                map.put("systemName", systemName);
                map.put("personId", personId);
                String jsonString = Y9JsonUtil.writeValueAsString(map);
                if (y9KafkaTemplate != null) {
                    y9KafkaTemplate.send(Y9TopicConst.Y9_DATACOPY4SYSTEM_MESSAGE, jsonString);
                    LOGGER.info(Y9TopicConst.Y9_DATACOPY4SYSTEM_MESSAGE + "   ->" + jsonString);
                }
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

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
    public int deleteByTenantIdAndAppId(String tenantId, String appId) {
        List<Y9TenantApp> y9TenantAppList = y9TenantAppRepository.findByTenantIdAndAppId(tenantId, appId);
        int deleted = 0;
        for (Y9TenantApp ta : y9TenantAppList) {
            ta.setTenancy(false);
            if (StringUtils.isBlank(ta.getDeletedName())) {
                ta.setDeletedName(Y9LoginUserHolder.getUserInfo().getName());
                ta.setDeletedTime(new Date());
            }
            y9TenantAppRepository.save(ta);
            deleted++;
        }
        return deleted;
    }

    @Override
    public Y9TenantApp findById(String id) {
        return y9TenantAppRepository.findById(id).orElse(null);
    }

    public Y9TenantApp findByTenantIdAndAppIdAndVerify(String tenantId, String appId, Boolean verify, Boolean tenancy) {
        return y9TenantAppRepository.findByTenantIdAndAppIdAndVerifyAndTenancy(tenantId, appId, verify, tenancy);
    }

    @Override
    public Y9TenantApp getByTenantIdAndAppIdAndTenancy(String tenantId, String appId, Boolean tenancy) {
        return y9TenantAppManager.getByTenantIdAndAppIdAndTenancy(tenantId, appId, tenancy);
    }

    @Override
    public List<String> listAddedAppName(String tenantId, Boolean verify) {
        List<Y9TenantApp> list = y9TenantAppRepository.findByTenantIdAndVerify(tenantId, verify);
        if (list != null) {
            return list.stream().map(Y9TenantApp::getAppName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> listAppIdByTenantId(String tenantId, Boolean verify, Boolean tenancy) {
        List<Y9TenantApp> tas = y9TenantAppRepository.findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(tenantId, verify, tenancy);
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
    public List<String> listAppIdByTenantName(String tenantName) {
        List<Y9TenantApp> tas = y9TenantAppRepository.findByTenantName(tenantName);
        if (tas != null) {
            return tas.stream().map(Y9TenantApp::getAppId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> listAppNameByTenantId(String tenantId) {
        List<Y9TenantApp> list = y9TenantAppRepository.findByTenantId(tenantId);
        if (list != null) {
            return list.stream().map(Y9TenantApp::getAppName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Y9TenantApp> listByAppIdAndTenancy(String appId, Boolean tenancy) {
        return y9TenantAppRepository.findByAppIdAndTenancy(appId, tenancy);
    }

    @Override
    public List<Y9TenantApp> listByTenantIdAndAppId(String tenantId, String appId) {
        return y9TenantAppRepository.findByTenantIdAndAppId(tenantId, appId);
    }

    @Override
    public List<Y9TenantApp> listByTenantIdAndTenancy(String tenantId, Boolean verify, Boolean tenancy) {
        return y9TenantAppRepository.findByTenantIdAndVerifyAndTenancyOrderByCreateTimeDesc(tenantId, verify, tenancy);
    }

    @Override
    public Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime, String verifyTime, Boolean tenancy, String systemId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "verify").and(Sort.by(Sort.Direction.DESC, "createTime"));
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Y9TenantAppSpecification<Y9TenantApp> spec = new Y9TenantAppSpecification<>(verify, tenantName, createTime, verifyTime, tenancy, systemId);
        return y9TenantAppRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Y9TenantApp> page(Integer page, Integer rows, Boolean verify, String tenantName, String createTime, String verifyTime, Boolean tenancy, String appName, String systemIds) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime").and(Sort.by(Sort.Direction.ASC, "verify"));
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Y9TenantAppSpecification<Y9TenantApp> spec = new Y9TenantAppSpecification<>(verify, tenantName, createTime, verifyTime, tenancy, systemIds, appName);
        return y9TenantAppRepository.findAll(spec, pageable);
    }

    @Override
    public Page<Y9TenantApp> pageBySystemId(int page, int rows, String systemId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "appId").and(Sort.by(Sort.Direction.DESC, "createTime"));
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return y9TenantAppRepository.findBySystemIdAndTenancy(systemId, Boolean.TRUE, pageable);
    }

    @Override
    public Page<Y9TenantApp> pageByTenantId(int page, int rows, String tenantId) {
        if (page < 1) {
            page = 1;
        }
        Sort sort = Sort.by(Sort.Direction.ASC, "verify").and(Sort.by(Sort.Direction.DESC, "createTime"));
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return y9TenantAppRepository.findPageByTenantIdAndTenancyOrderByVerify(tenantId, Boolean.TRUE, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TenantApp save(String appId, String tenantId, String applyName, String applyReason) {
        Y9TenantApp y9TenantApp = y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, Boolean.TRUE);
        Y9App y9App = y9AppRepository.findById(appId).orElse(null);
        String tenantDataSource = y9TenantSystemManager.getDataSourceIdByTenantIdAndSystemId(tenantId, y9App.getSystemId());
        if (null != y9TenantApp) {
            if (tenantDataSource != null && !y9TenantApp.getVerify()) {
                y9TenantApp.setVerify(true);
                return save(y9TenantApp);
            }
            return y9TenantApp;
        }
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();

        Y9System y9System = y9SystemRepository.findById(y9App.getSystemId()).orElse(null);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Y9TenantApp ta = new Y9TenantApp();
        String guid = Y9IdGenerator.genId(IdType.SNOWFLAKE);
        ta.setId(guid);
        ta.setTenantId(tenantId);
        ta.setTenantName(y9TenantManager.getById(tenantId).getName());
        ta.setAppId(appId);
        ta.setSystemId(y9App.getSystemId());
        ta.setAppName(y9App.getName());
        ta.setApplyName(applyName);
        ta.setApplyId(userInfo.getPersonId());
        ta.setApplyReason(applyReason);
        ta.setTenancy(Boolean.TRUE);
        // 审核状态
        if (StringUtils.isNotBlank(tenantDataSource)) {
            ta.setVerify(Boolean.TRUE);
            ta.setVerifyUserName(Y9LoginUserHolder.getUserInfo().getName());
            ta.setVerifyTime(time);
            ta.setReason("同意申请");
            // 流程类应用从开发商对应的租户复制流程管理和事项管理的数据到租用的租户
            if (y9App.getType() == 2) {
                copyItemData(tenantId, y9System, y9App, ta.getApplyId());
            }
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
    public int updateByAppIdAndTenantId(Boolean tenancy, String deletedName, Date deletedTime, String appId, String tenantId, Boolean currentTenancy) {
        try {
            Y9TenantApp ta = y9TenantAppRepository.findByTenantIdAndAppIdAndTenancy(tenantId, appId, currentTenancy);
            if (null != ta) {
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
    public int updateStatus(Boolean verify, String id) {
        try {
            Y9TenantApp ta = this.findById(id);
            ta.setVerify(verify);
            ta.setVerifyTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            y9TenantAppRepository.save(ta);
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
}
