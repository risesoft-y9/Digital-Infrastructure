package net.risesoft.service.org.impl;

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
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.org.Y9ManagerRepository;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.service.setting.Y9SettingService;
import net.risesoft.util.Y9OrgUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9platform.Y9PlatformProperties;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9.util.signing.Y9MessageDigest;

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

    @Override
    @Transactional
    public Y9Manager changeDisabled(String id) {
        Y9Manager currentManager = this.getById(id);
        Y9Manager originalManager = Y9ModelConvertUtil.convert(currentManager, Y9Manager.class);
        boolean disableStatusToUpdate = !currentManager.getDisabled();

        currentManager.setDisabled(disableStatusToUpdate);
        Y9Manager savedManager = this.update(currentManager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_UPDATE_DISABLED.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_UPDATE_DISABLED.getDescription(),
                savedManager.getManagerLevel().getName(), savedManager.getName(), disableStatusToUpdate ? "禁用" : "启用"))
            .objectId(id)
            .oldObject(originalManager)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedManager;
    }

    @Override
    @Transactional
    public void changePassword(String id, String newPassword) {
        Y9Manager currentManager = this.getById(id);
        Y9Manager originalManager = Y9ModelConvertUtil.convert(currentManager, Y9Manager.class);

        currentManager.setPassword(Y9MessageDigest.bcrypt(newPassword));
        currentManager.setLastModifyPasswordTime(new Date());
        Y9Manager savedManager = this.update(currentManager);

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
    public boolean checkPassword(String personId, String password) {
        Y9Manager manager = this.getById(personId);
        return Y9MessageDigest.bcryptMatch(password, manager.getPassword());
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
        Y9Manager y9Manager = this.getById(id);
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
    public Optional<Y9Manager> findById(String id) {
        return y9ManagerRepository.findById(id);
    }

    @Override
    public Optional<Y9Manager> findByLoginName(String loginName) {
        return y9ManagerRepository.findByLoginName(loginName);
    }

    @Override
    public Y9Manager getById(String id) {
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
        Y9Manager y9Manager = this.getById(managerId);
        if (Boolean.TRUE.equals(y9Manager.getGlobalManager())) {
            return true;
        } else {
            if (Objects.equals(deptId, y9Manager.getParentId())) {
                // deptId 对应部门的三员
                return true;
            }
            Y9Department targetDepartment = y9DepartmentManager.getByIdFromCache(deptId);
            Y9Department managerDept = y9DepartmentManager.getByIdFromCache(y9Manager.getParentId());
            // 部门三员管理 parentId 对应的部门及其后代部门
            return Y9OrgUtil.isDescendantOf(targetDepartment, managerDept);
        }
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
        Y9Manager y9Manager = this.getById(id);
        Date lastModifyPasswordTime = y9Manager.getLastModifyPasswordTime();
        if (lastModifyPasswordTime == null) {
            return true;
        }
        long daysBetween = DateUtil.between(lastModifyPasswordTime, new Date(), DateUnit.DAY);
        return daysBetween >= this.getPasswordModifiedCycle(y9Manager.getManagerLevel());
    }

    @Override
    public List<Y9Manager> listAll() {
        return y9ManagerRepository.findAll();
    }

    @Override
    public List<Y9Manager> listByGlobalManager(boolean globalManager) {
        return y9ManagerRepository.findByGlobalManager(globalManager);
    }

    @Override
    public List<Y9Manager> listByParentId(String parentId) {
        return y9ManagerRepository.findByParentIdOrderByTabIndex(parentId);
    }

    @Override
    @Transactional
    public Y9Manager resetDefaultPassword(String id) {
        Y9Manager currentManager = this.getById(id);
        Y9Manager originalManager = Y9ModelConvertUtil.convert(currentManager, Y9Manager.class);
        String defaultPassword = y9SettingService.getTenantSetting().getUserDefaultPassword();

        currentManager.setPassword(Y9MessageDigest.bcrypt(defaultPassword));
        Y9Manager savedManager = this.update(currentManager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_RESET_PASSWORD.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_RESET_PASSWORD.getDescription(),
                savedManager.getManagerLevel().getName(), savedManager.getName()))
            .objectId(id)
            .oldObject(originalManager)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedManager;
    }

    @Transactional
    public Y9Manager insert(Y9Manager y9Manager) {
        String defaultPassword = y9SettingService.getTenantSetting().getUserDefaultPassword();
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Manager.getParentId());

        if (StringUtils.isBlank(y9Manager.getId())) {
            y9Manager.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9Manager.setTenantId(Y9LoginUserHolder.getTenantId());
        y9Manager.setTabIndex(compositeOrgBaseManager.getNextSubTabIndex(y9Manager.getParentId()));
        // 系统管理员新建的子域三员默认禁用 需安全管理员启用
        y9Manager.setDisabled(!y9Manager.getGlobalManager());
        y9Manager.setPassword(Y9MessageDigest.bcrypt(defaultPassword));
        y9Manager.setDn(Y9OrgUtil.buildDn(OrgTypeEnum.MANAGER, y9Manager.getName(), parent.getDn()));
        y9Manager.setGuidPath(Y9OrgUtil.buildGuidPath(parent.getGuidPath(), y9Manager.getId()));
        y9Manager.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(y9Manager));
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Manager));
        return y9Manager;
    }

    @Transactional
    public Y9Manager update(Y9Manager y9Manager) {
        Y9Manager currentManager = this.getById(y9Manager.getId());
        Y9Manager originalManager = Y9ModelConvertUtil.convert(currentManager, Y9Manager.class);
        Y9BeanUtil.copyProperties(y9Manager, currentManager);
        Y9Manager savedManager = y9ManagerRepository.save(currentManager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(originalManager, savedManager));

        return savedManager;
    }

    @Override
    @Transactional
    public Y9Manager saveOrUpdate(Y9Manager y9Manager) {
        if (StringUtils.isNotBlank(y9Manager.getId())) {
            Y9Manager oldManager = y9ManagerRepository.findById(y9Manager.getId()).orElse(null);
            if (oldManager != null) {
                Y9Manager originalManager = Y9ModelConvertUtil.convert(oldManager, Y9Manager.class);
                Y9Manager savedManager = this.update(y9Manager);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.MANAGER_UPDATE.getAction())
                    .description(
                        Y9StringUtil.format(AuditLogEnum.MANAGER_UPDATE.getDescription(), savedManager.getName()))
                    .objectId(savedManager.getId())
                    .oldObject(originalManager)
                    .currentObject(savedManager)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedManager;
            }
        }

        Y9Manager savedManager = this.insert(y9Manager);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.MANAGER_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.MANAGER_CREATE.getDescription(), savedManager.getName()))
            .objectId(savedManager.getId())
            .oldObject(null)
            .currentObject(savedManager)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return savedManager;
    }

    @Override
    @Transactional
    public void updateCheckTime(String managerId, Date checkTime) {
        Y9Manager y9Manager = this.getById(managerId);
        y9Manager.setLastReviewLogTime(checkTime);
        y9ManagerRepository.save(y9Manager);
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
        List<Y9Manager> y9ManagerList = listByParentId(parentId);
        for (Y9Manager y9Manager : y9ManagerList) {
            this.delete(y9Manager.getId());
        }
    }
}
