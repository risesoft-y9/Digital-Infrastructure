package net.risesoft.service.org.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OrgLevelConsts;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Manager;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.exception.OrgUnitErrorCodeEnum;
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
public class Y9ManagerServiceImpl implements Y9ManagerService {

    private static final int MOBILE_NUMBER_LENGTH = 11;

    private final Y9ManagerRepository y9ManagerRepository;
    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9DepartmentManager y9DepartmentManager;
    private final Y9Properties y9config;

    @Override
    @Transactional(readOnly = false)
    public Y9Manager changeDisabled(String id) {
        Y9Manager y9Manager = this.getById(id);
        y9Manager.setDisabled(!y9Manager.getDisabled());
        y9Manager = saveOrUpdate(y9Manager);
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
    public boolean isLoginNameAvailable(final String id, final String loginName) {
        if (StringUtils.isNotEmpty(id)) {
            return y9ManagerRepository.existsById(id);
        }

        Optional<Y9Manager> y9ManagerOptional = y9ManagerRepository.findByLoginName(loginName);
        return y9ManagerOptional.isEmpty();
    }

    @Override
    public boolean checkPassword(String personId, String password) {
        Y9Manager manager = this.getById(personId);
        return Y9MessageDigest.checkpw(password, manager.getPassword());
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(List<String> ids) {
        for (String id : ids) {
            this.delete(id);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        Y9Manager y9Manager = this.getById(id);
        y9ManagerRepository.delete(y9Manager);

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
    @Transactional(readOnly = false)
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
        String password = y9config.getCommon().getDefaultPassword();
        y9Manager.setPassword(Y9MessageDigest.hashpw(password));
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Manager, y9Manager));
        return y9Manager;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Manager resetDefaultPassword(String id) {
        Y9Manager y9Manager = this.getById(id);
        y9Manager.setPassword(Y9MessageDigest.hashpw(y9config.getCommon().getDefaultPassword()));
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Manager, y9Manager));
        return y9Manager;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Manager saveOrUpdate(Y9Manager y9Manager) {
        String password = y9config.getCommon().getDefaultPassword();
        Y9OrgBase parent = compositeOrgBaseManager.getOrgUnitAsParent(y9Manager.getParentId());
        if (StringUtils.isNotBlank(y9Manager.getId())) {
            Y9Manager oldManager = y9ManagerRepository.findById(y9Manager.getId()).orElse(null);
            if (oldManager != null) {
                Y9BeanUtil.copyProperties(y9Manager, oldManager);
                oldManager = y9ManagerRepository.save(oldManager);

                Y9Context.publishEvent(new Y9EntityUpdatedEvent<>(y9Manager, oldManager));
                return oldManager;
            }
        } else {
            y9Manager.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        y9Manager.setTenantId(Y9LoginUserHolder.getTenantId());
        y9Manager.setTabIndex(compositeOrgBaseManager.getMaxSubTabIndex(y9Manager.getParentId(), OrgTypeEnum.MANAGER));
        y9Manager.setDn(OrgLevelConsts.getOrgLevel(OrgTypeEnum.MANAGER) + y9Manager.getName() + OrgLevelConsts.SEPARATOR
            + parent.getDn());
        // 系统管理员新建的子域三员默认禁用 需安全管理员启用
        y9Manager.setDisabled(y9Manager.getGlobalManager() ? false : true);

        y9Manager.setPassword(Y9MessageDigest.hashpw(password));
        y9Manager.setGuidPath(compositeOrgBaseManager.buildGuidPath(y9Manager));
        y9Manager.setOrderedPath(compositeOrgBaseManager.buildOrderedPath(y9Manager));
        y9Manager.setOrgType(OrgTypeEnum.MANAGER.getEnName());
        y9Manager = y9ManagerRepository.save(y9Manager);

        Y9Context.publishEvent(new Y9EntityCreatedEvent<>(y9Manager));
        return y9Manager;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateCheckTime(String managerId, Date checkTime) {
        Y9Manager y9Manager = this.getById(managerId);
        y9Manager.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkTime));
        y9ManagerRepository.save(y9Manager);
    }
}
