package net.risesoft.service.org.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.ManagerLevelEnum;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.enums.SexEnum;
import net.risesoft.exception.ManagerErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.manager.org.Y9DepartmentManager;
import net.risesoft.repository.Y9ManagerRepository;
import net.risesoft.service.org.Y9ManagerService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityCreatedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.pubsub.event.Y9EntityUpdatedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.signing.Y9MessageDigest;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9ManagerImpl implements Y9ManagerService {

    private static final int MOBILE_NUMBER_LENGTH = 11;

    private final Y9ManagerRepository y9ManagerRepository;
    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9DepartmentManager y9DepartmentManager;
    private final Y9Properties y9config;

    @Override
    @Transactional(readOnly = false)
    public Y9Manager changeDisabled(String id) {
        Y9Manager y9Manager = this.findById(id);
        y9Manager.setDisabled(!y9Manager.getDisabled());
        y9Manager = saveOrUpdate(y9Manager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<Y9Manager>(y9Manager, y9Manager));

        return y9Manager;
    }

    @Override
    @Transactional(readOnly = false)
    public void changePassword(String id, String newPassword) {
        newPassword = Y9MessageDigest.hashpw(newPassword);
        Y9Manager manager = this.getById(id);
        manager.setPassword(newPassword);
        manager.setModifyPwdTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        Y9Manager y9Manager = y9ManagerRepository.save(manager);
        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(manager, y9Manager));
    }

    @Override
    public boolean isDeptManager(String managerId, String deptId) {
        Y9Manager y9Manager = this.getById(managerId);
        if (Boolean.TRUE.equals(y9Manager.getGlobalManager())) {
            return true;
        } else {
            // 验证是否为某个部门的三员（小三员）
            Y9Department targetDepartment = y9DepartmentManager.getById(deptId);
            Y9Department managerDept = y9DepartmentManager.getById(y9Manager.getParentId());
            String targetDepartmentGuidPath = targetDepartment.getGuidPath();
            String managerDepartmentGuidPath = managerDept.getGuidPath();
            return targetDepartmentGuidPath.contains(managerDepartmentGuidPath);
        }
    }

    @Override
    public boolean checkLoginName(final String id, final String loginName) {
        if (StringUtils.isNotEmpty(id)) {
            return y9ManagerRepository.existsById(id);
        }

        List<Y9Manager> managerList = y9ManagerRepository.findByLoginName(loginName);
        return managerList.isEmpty();
    }

    @Override
    @Transactional(readOnly = false)
    public void createAuditManager(String id, String tenantId, String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (!existsById(id)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9Manager auditManager = new Y9Manager();
            auditManager.setId(id);
            auditManager.setTenantId(tenantId);
            auditManager.setParentId(organizationId);
            auditManager.setDisabled(false);
            auditManager.setName(ManagerLevelEnum.AUDIT_MANAGER.getName());
            auditManager.setLoginName("auditManager");
            auditManager.setSex(SexEnum.MALE.getValue());
            auditManager.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
            auditManager.setDn("cn=" + ManagerLevelEnum.AUDIT_MANAGER.getName() + ",o=组织");
            auditManager.setOrgType(OrgTypeEnum.MANAGER.getEnName());
            auditManager.setGuidPath(organizationId + "," + id);
            auditManager.setTabIndex(10002);
            auditManager.setGlobalManager(true);
            auditManager.setManagerLevel(ManagerLevelEnum.AUDIT_MANAGER.getValue());
            auditManager.setPwdCycle(7);
            auditManager.setUserHostIp("");
            auditManager.setCheckTime(sdf.format(new Date()));
            auditManager.setModifyPwdTime(sdf.format(new Date()));
            auditManager.setCheckCycle(7);
            this.saveOrUpdate(auditManager);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void createSecurityManager(String id, String tenantId, String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (!this.existsById(id)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9Manager securityManager = new Y9Manager();
            securityManager.setId(id);
            securityManager.setTenantId(tenantId);
            securityManager.setParentId(organizationId);
            securityManager.setDisabled(false);
            securityManager.setName(ManagerLevelEnum.SECURITY_MANAGER.getName());
            securityManager.setLoginName("securityManager");
            securityManager.setSex(SexEnum.MALE.getValue());
            securityManager.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
            securityManager.setDn("cn=" + ManagerLevelEnum.SECURITY_MANAGER.getName() + ",o=组织");
            securityManager.setOrgType(OrgTypeEnum.MANAGER.getEnName());
            securityManager.setGuidPath(organizationId + "," + id);
            securityManager.setTabIndex(10001);
            securityManager.setGlobalManager(true);
            securityManager.setManagerLevel(ManagerLevelEnum.SECURITY_MANAGER.getValue());
            securityManager.setPwdCycle(7);
            securityManager.setUserHostIp("");
            securityManager.setCheckTime(sdf.format(new Date()));
            securityManager.setModifyPwdTime(sdf.format(new Date()));
            securityManager.setCheckCycle(7);
            this.saveOrUpdate(securityManager);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void createSystemManager(String managerId, String tenantId, String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        if (!this.existsById(managerId)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9Manager systemManager = new Y9Manager();
            systemManager.setId(managerId);
            systemManager.setTenantId(tenantId);
            systemManager.setParentId(organizationId);
            systemManager.setDisabled(false);
            systemManager.setName(ManagerLevelEnum.SYSTEM_MANAGER.getName());
            systemManager.setLoginName("systemManager");
            systemManager.setSex(SexEnum.MALE.getValue());
            systemManager.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
            systemManager.setDn("cn=" + ManagerLevelEnum.SYSTEM_MANAGER.getName() + ",o=组织");
            systemManager.setOrgType(OrgTypeEnum.MANAGER.getEnName());
            systemManager.setGuidPath(organizationId + "," + managerId);
            systemManager.setTabIndex(10000);
            systemManager.setGlobalManager(true);
            systemManager.setManagerLevel(ManagerLevelEnum.SYSTEM_MANAGER.getValue());
            systemManager.setPwdCycle(7);
            systemManager.setUserHostIp("");
            systemManager.setCheckTime(sdf.format(new Date()));
            systemManager.setModifyPwdTime(sdf.format(new Date()));
            systemManager.setCheckCycle(0);
            this.saveOrUpdate(systemManager);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Manager y9Manager = this.getById(id);
        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Manager));
        y9ManagerRepository.delete(y9Manager);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String[] ids) {
        for (String id : ids) {
            this.delete(id);
        }
    }

    @Override
    public boolean existsById(String id) {
        return y9ManagerRepository.existsById(id);
    }

    @Override
    public Y9Manager findById(String id) {
        return y9ManagerRepository.findById(id).orElse(null);
    }

    @Override
    public Y9Manager getById(String id) {
        return y9ManagerRepository.findById(id)
            .orElseThrow(() -> Y9ExceptionUtil.notFoundException(ManagerErrorCodeEnum.MANAGER_NOT_FOUND, id));
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

    @EventListener
    public void onParentDepartmentDeleted(Y9EntityDeletedEvent<Y9Department> event) {
        Y9Department parentDepartment = event.getEntity();
        // 删除部门时其下管理员也要删除
        removeByParentId(parentDepartment.getId());
    }

    @Transactional(readOnly = false)
    public void removeByParentId(String parentId) {
        List<Y9Manager> y9ManagerList = listByParentId(parentId);
        for (Y9Manager y9Manager : y9ManagerList) {
            this.delete(y9Manager.getId());
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Manager resetPassword(String id) {
        Y9Manager y9Manager = this.getById(id);
        String mobile = y9Manager.getMobile();
        String password = y9config.getCommon().getDefaultPassword();
        if (mobile != null && mobile.length() == MOBILE_NUMBER_LENGTH) {
            password = mobile.substring(mobile.length() - 6);
        }
        y9Manager.setPassword(Y9MessageDigest.hashpw(password));
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Manager, y9Manager));
        return y9Manager;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Manager resetPasswordToDefault(String id) {
        Y9Manager y9Manager = this.getById(id);
        y9Manager.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Manager, y9Manager));
        return y9Manager;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Manager saveOrUpdate(Y9Manager y9Manager) {
        String mobile = y9Manager.getMobile();
        String password = y9config.getCommon().getDefaultPassword();
        Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgBase(y9Manager.getParentId());
        if (StringUtils.isNotBlank(y9Manager.getId())) {
            Y9Manager oldManager = y9ManagerRepository.findById(y9Manager.getId()).orElse(null);
            if (oldManager != null) {
                Y9BeanUtil.copyProperties(y9Manager, oldManager);
                oldManager = y9ManagerRepository.save(oldManager);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<Y9Manager>(y9Manager, oldManager));
                return oldManager;
            } else {
                y9Manager.setTenantId(Y9LoginUserHolder.getTenantId());
                y9Manager.setTabIndex(
                    compositeOrgBaseManager.getMaxSubTabIndex(y9Manager.getParentId(), OrgTypeEnum.MANAGER));
                y9Manager.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.MANAGER) + y9Manager.getName()
                    + OrgLevelConsts.SEPARATOR + y9OrgBase.getDn());
                y9Manager.setDisabled(false);
                y9Manager.setOrgType(OrgTypeEnum.MANAGER.getEnName());
                if (mobile != null && mobile.length() == MOBILE_NUMBER_LENGTH) {
                    password = mobile.substring(mobile.length() - 6);
                }
                y9Manager.setPassword(Y9MessageDigest.hashpw(password));
                StringBuilder sb = new StringBuilder();
                compositeOrgBaseManager.getGuidPathRecursiveUp(sb, y9Manager);
                y9Manager.setGuidPath(sb.toString());

                sb = new StringBuilder();
                compositeOrgBaseManager.getOrderedPathRecursiveUp(sb, y9Manager);
                y9Manager.setOrderedPath(sb.toString());
                y9Manager = y9ManagerRepository.save(y9Manager);

                Y9Context.publishEvent(new Y9EntityCreatedEvent<Y9Manager>(y9Manager));
                return y9Manager;
            }
        }
        y9Manager.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9Manager.setTenantId(Y9LoginUserHolder.getTenantId());
        y9Manager.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(y9Manager.getParentId(), OrgTypeEnum.MANAGER));
        y9Manager.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.MANAGER) + y9Manager.getName() + OrgLevelConsts.SEPARATOR
            + y9OrgBase.getDn());
        y9Manager.setDisabled(true);
        if (mobile != null && mobile.length() == MOBILE_NUMBER_LENGTH) {
            password = mobile.substring(mobile.length() - 6);
        }
        y9Manager.setPassword(Y9MessageDigest.hashpw(password));
        StringBuilder sb = new StringBuilder();
        compositeOrgBaseManager.getGuidPathRecursiveUp(sb, y9Manager);
        y9Manager.setGuidPath(sb.toString());

        sb = new StringBuilder();
        compositeOrgBaseManager.getOrderedPathRecursiveUp(sb, y9Manager);
        y9Manager.setOrderedPath(sb.toString());
        y9Manager.setOrgType(OrgTypeEnum.MANAGER.getEnName());
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Manager));
        return y9Manager;
    }
}
