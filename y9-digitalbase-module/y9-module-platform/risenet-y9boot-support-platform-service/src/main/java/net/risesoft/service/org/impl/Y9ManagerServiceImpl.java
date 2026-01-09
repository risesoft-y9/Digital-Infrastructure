package net.risesoft.service.org.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.org.Y9Department;
import net.risesoft.entity.org.Y9Manager;
import net.risesoft.entity.org.Y9OrgBase;
import net.risesoft.enums.AuditLogEnum;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.model.platform.org.Manager;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9ManagerRepository;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9.util.signing.Y9MessageDigestUtil;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9ManagerServiceImpl implements Y9ManagerService {

    private final Y9ManagerRepository y9ManagerRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9DepartmentManager y9DepartmentManager;
    private final Y9SettingService y9SettingService;

    private final Y9PlatformProperties y9PlatformProperties;

    private static Manager entityToModel(Y9Manager y9Manager) {
        return PlatformModelConvertUtil.convert(y9Manager, Manager.class);
    }

    private static List<Manager> entityToModel(List<Y9Manager> y9ManagerList) {
        return PlatformModelConvertUtil.convert(y9ManagerList, Manager.class);
    }

    @Override
    @Transactional
    public Manager changeDisabled(String id) {
        Y9Manager currentManager = this.get(id);
        Y9Manager originalManager = PlatformModelConvertUtil.convert(currentManager, Y9Manager.class);

        Boolean disableStatus = currentManager.changeDisabled();
        Y9Manager savedManager = this.update(currentManager, originalManager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_UPDATE_DISABLED.getDescription(),
                savedManager.getManagerLevel().getName(), savedManager.getName(), disableStatus ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalManager)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedManager);
    }

    @Override
    @Transactional
    public void changePassword(String id, String newPassword) {
        Y9Manager currentManager = this.get(id);
        Y9Manager originalManager = PlatformModelConvertUtil.convert(currentManager, Y9Manager.class);

        currentManager.changePassword(newPassword);
        Y9Manager savedManager = this.update(currentManager, originalManager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_UPDATE_PASSWORD.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_UPDATE_PASSWORD.getDescription(),
                savedManager.getManagerLevel().getName(), savedManager.getName()))
            .objectId(id)
            .oldObject(originalManager)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    public boolean isPasswordCorrect(String personId, String password) {
        Y9Manager manager = this.get(personId);
        return manager.isPasswordCorrect(password);
    }

    @Override
    @Transactional
    public void delete(List<String> ids) {
        for (String id : ids) {
            this.delete(id);
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        Y9Manager y9Manager = this.get(id);
        y9ManagerRepository.delete(y9Manager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_DELETE.getDescription(),
                y9Manager.getManagerLevel().getName(), y9Manager.getName()))
            .objectId(id)
            .oldObject(y9Manager)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Manager));
    }

    @Override
    public boolean existsById(String id) {
        return y9ManagerRepository.existsById(id);
    }

    @Override
    public boolean existsByLoginName(String loginName) {
        return y9ManagerRepository.existsByLoginName(loginName);
    }

    @Override
    public Optional<Manager> findById(String id) {
        return y9ManagerRepository.findById(id).map(y9Manager -> entityToModel(y9Manager));
    }

    @Override
    public Optional<Manager> findByLoginName(String loginName) {
        return y9ManagerRepository.findByLoginName(loginName).map(y9Manager -> entityToModel(y9Manager));
    }

    @Override
    public Manager getById(String id) {
        Y9Manager y9Manager = this.get(id);
        return entityToModel(y9Manager);
    }

    private Y9Manager get(String id) {
        return y9ManagerRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(OrgUnitErrorCodeEnum.MANAGER_NOT_FOUND, id));
    }

    @Override
    public int getPasswordModifiedCycle(ManagerLevelEnum managerLevel) {
        return y9PlatformProperties.getManagerModifyPasswordCycle();
    }

    @Override
    public int getReviewLogCycle(ManagerLevelEnum managerLevel) {
        int checkCycle = 0;
        if (ManagerLevelEnum.SYSTEM_MANAGER.equals(managerLevel)) {
            checkCycle = y9PlatformProperties.getSystemManagerReviewLogCycle();
        }
        if (ManagerLevelEnum.SECURITY_MANAGER.equals(managerLevel)) {
            checkCycle = y9PlatformProperties.getSecurityManagerReviewLogCycle();
        }
        if (ManagerLevelEnum.AUDIT_MANAGER.equals(managerLevel)) {
            checkCycle = y9PlatformProperties.getAuditManagerReviewLogCycle();
        }
        return checkCycle;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDeptManager(String managerId, String deptId) {
        Y9Manager y9Manager = this.get(managerId);
        Y9OrgBase targetOrgBase = compositeOrgBaseManager.getOrgUnitAsParent(deptId);
        Y9OrgBase managerParent = compositeOrgBaseManager.getOrgUnitAsParent(y9Manager.getParentId());

        return y9Manager.isDeptManagerOf(managerParent, targetOrgBase);
    }

    @Override
    public boolean isLoginNameAvailable(final String id, final String loginName) {
        if (StringUtils.isNotEmpty(id)) {
            return y9ManagerRepository.existsById(id);
        }

        Optional<Y9Manager> y9ManagerOptional = y9ManagerRepository.findByLoginName(loginName);
        return y9ManagerOptional.isEmpty();
    }

    @Override
    public Boolean isPasswordExpired(String id) {
        Y9Manager y9Manager = this.get(id);
        int passwordModifiedCycle = this.getPasswordModifiedCycle(y9Manager.getManagerLevel());
        return y9Manager.isPasswordExpired(passwordModifiedCycle);
    }

    @Override
    public List<Manager> listAll() {
        List<Y9Manager> y9ManagerList = y9ManagerRepository.findAll();
        return entityToModel(y9ManagerList);
    }

    @Override
    public List<Manager> listByGlobalManager(boolean globalManager) {
        List<Y9Manager> y9ManagerList = y9ManagerRepository.findByGlobalManager(globalManager);
        return entityToModel(y9ManagerList);
    }

    @Override
    public List<Manager> listByParentId(String parentId) {
        List<Y9Manager> y9ManagerList = y9ManagerRepository.findByParentIdOrderByTabIndex(parentId);
        return PlatformModelConvertUtil.convert(y9ManagerList, Manager.class);
    }

    @Override
    @Transactional
    public Manager resetDefaultPassword(String id) {
        Y9Manager currentManager = this.get(id);
        Y9Manager originalManager = PlatformModelConvertUtil.convert(currentManager, Y9Manager.class);
        String defaultPassword = y9SettingService.getTenantSetting().getUserDefaultPassword();

        currentManager.changePassword(defaultPassword);
        Y9Manager savedManager = this.update(currentManager, originalManager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_RESET_PASSWORD.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_RESET_PASSWORD.getDescription(),
                savedManager.getManagerLevel().getName(), savedManager.getName()))
            .objectId(id)
            .oldObject(originalManager)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedManager);
    }

    @Transactional
    public Y9Manager insert(Y9Manager y9Manager) {
        Y9Manager savedManager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(savedManager));
        
        return savedManager;
    }

    @Transactional
    public Y9Manager update(Y9Manager y9Manager, Y9Manager originalManager) {
        Y9Manager savedManager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalManager, savedManager));

        return savedManager;
    }

    @Override
    @Transactional
    public Manager saveOrUpdate(Manager manager) {
        if (StringUtils.isNotBlank(manager.getId())) {
            Optional<Y9Manager> y9ManagerOptional = y9ManagerRepository.findById(manager.getId());
            if (y9ManagerOptional.isPresent()) {
                Y9Manager originalManager = PlatformModelConvertUtil.convert(y9ManagerOptional.get(), Y9Manager.class);
                Y9Manager y9Manager = y9ManagerOptional.get();
                
                Y9BeanUtil.copyProperties(manager, y9Manager);
                
                Y9Manager savedManager = this.update(y9Manager, originalManager);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.MANAGER_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.MANAGER_UPDATE.getDescription(), savedManager.getName()))
                    .objectId(savedManager.getId())
                    .oldObject(originalManager)
                    .currentObject(savedManager)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return entityToModel(savedManager);
            }
        }


        String defaultPassword = y9SettingService.getTenantSetting().getUserDefaultPassword();
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(manager.getParentId());
        Integer nextSubTabIndex = compositeOrgBaseManager.getNextSubTabIndex(manager.getParentId());
        List<Y9OrgBase> ancestorList = compositeOrgBaseManager.listOrgUnitAndAncestor(manager.getParentId());

        Y9Manager y9Manager = new Y9Manager(manager, parent, nextSubTabIndex, ancestorList, defaultPassword);
        Y9Manager savedManager = this.insert(y9Manager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_CREATE.getDescription(), savedManager.getName()))
            .objectId(savedManager.getId())
            .oldObject(null)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedManager);
    }

    @Override
    @Transactional
    public void updateCheckTime(String managerId, Date checkTime) {
        Y9Manager y9Manager = this.get(managerId);
        y9Manager.setLastReviewLogTime(checkTime);
        y9ManagerRepository.save(y9Manager);
    }

    @Override
    public List<OrgUnit> filterManagableOrgUnitList(String managerParentId, List<String> orgUnitIdList) {
        List<OrgUnit> managableOrgUnitList = new ArrayList<>();
        Y9OrgBase managerDept = compositeOrgBaseManager.getOrgUnitAsParent(managerParentId);
        for (String orgUnitId : orgUnitIdList) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgUnit(orgUnitId);
            if (Y9OrgUtil.isSameOf(managerDept, y9OrgBase) || Y9OrgUtil.isAncestorOf(managerDept, y9OrgBase)) {
                managableOrgUnitList.add(PlatformModelConvertUtil.orgBaseToOrgUnit(y9OrgBase));
            }
        }
        return managableOrgUnitList;
    }

    @EventListener
    @Transactional
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下管理员也要删除
        removeByParentId(parentDepartment.getId());
    }

    @Transactional
    public void removeByParentId(String parentId) {
        List<Manager> managerList = listByParentId(parentId);
        for (Manager manager : managerList) {
            this.delete(manager.getId());
        }
    }
}
