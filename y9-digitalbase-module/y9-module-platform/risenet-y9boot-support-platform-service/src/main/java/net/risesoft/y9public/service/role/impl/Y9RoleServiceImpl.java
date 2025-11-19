package net.risesoft.y9public.service.role.impl;

import static net.risesoft.consts.JpaPublicConsts.PUBLIC_TRANSACTION_MANAGER;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.AuditLogEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.exception.RoleErrorCodeEnum;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.permission.Y9OrgBasesToRolesRepository;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.exception.util.Y9ExceptionUtil;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.Y9Role;
import net.risesoft.y9public.entity.Y9System;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.manager.resource.Y9AppManager;
import net.risesoft.y9public.manager.resource.Y9SystemManager;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.Y9RoleRepository;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Service
@RequiredArgsConstructor
public class Y9RoleServiceImpl implements Y9RoleService {

    private final Y9RoleRepository y9RoleRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;

    private final Y9RoleManager y9RoleManager;
    private final Y9AppManager y9AppManager;
    private final Y9SystemManager y9SystemManager;

    private static Role entityToModel(Y9Role y9Role) {
        return PlatformModelConvertUtil.y9RoleToRole(y9Role);
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    @Override
    public void delete(String id) {
        Y9Role y9Role = y9RoleManager.getByIdFromCache(id);

        y9RoleManager.delete(id);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ROLE_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ROLE_DELETE.getDescription(), y9Role.getName()))
            .objectId(id)
            .oldObject(y9Role)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        Y9Context.publishEvent(new Y9EntityDeletedEvent<>(y9Role));
    }

    @Override
    public Optional<Role> findByCustomIdAndParentId(String customId, String parentId) {
        return y9RoleRepository.findByCustomIdAndParentId(customId, parentId).map(Y9RoleServiceImpl::entityToModel);
    }

    @Override
    public Optional<Role> findById(String id) {
        return y9RoleManager.findByIdFromCache(id).map(Y9RoleServiceImpl::entityToModel);
    }

    @Override
    public Role getById(String roleId) {
        return entityToModel(y9RoleManager.getByIdFromCache(roleId));
    }

    @Override
    public List<Role> listByName(String name) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByNameContainingOrderByTabIndexAsc(name);
        return entityToModel(y9RoleList);
    }

    @Override
    public List<Role> listByParentId(String parentId) {
        List<Y9Role> y9RoleList = y9RoleRepository.findByParentIdOrderByTabIndexAsc(parentId);
        return entityToModel(y9RoleList);
    }

    @Override
    public List<Role> listByParentId4Tenant(String parentId, String tenantId) {
        List<Y9Role> y9RoleList = y9RoleRepository
            .findByParentIdAndTenantIdOrParentIdAndTenantIdIsNullOrderByTabIndexAsc(parentId, tenantId, parentId);
        return entityToModel(y9RoleList);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void move(String id, String newParentId) {
        Y9Role currentRole = y9RoleManager.getByIdFromCache(id);
        String parentName = this.getRoleParent(newParentId);
        Y9Role originalRole = PlatformModelConvertUtil.convert(currentRole, Y9Role.class);

        currentRole.setParentId(newParentId);
        Y9Role savedRole = y9RoleManager.update(currentRole);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ROLE_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ROLE_UPDATE_PARENTID.getDescription(), savedRole.getName(),
                parentName))
            .objectId(id)
            .oldObject(originalRole)
            .currentObject(savedRole)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        recursiveUpdateByDn(entityToModel(savedRole));
    }

    private String getRoleParent(String newParentId) {
        Optional<Y9Role> y9RoleOptional = y9RoleManager.findByIdFromCache(newParentId);
        if (y9RoleOptional.isPresent()) {
            return y9RoleOptional.get().getName();
        }

        Optional<Y9App> y9AppOptional = y9AppManager.findByIdFromCache(newParentId);
        if (y9AppOptional.isPresent()) {
            return y9AppOptional.get().getName();
        }

        Optional<Y9System> y9SystemOptional = y9SystemManager.findByIdFromCache(newParentId);
        if (y9SystemOptional.isPresent()) {
            return y9SystemOptional.get().getName();
        }

        throw Y9ExceptionUtil.notFoundException(RoleErrorCodeEnum.ROLE_PARENT_NOT_FOUND, newParentId);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Role saveOrUpdate(Role role) {
        Y9Role y9Role = PlatformModelConvertUtil.convert(role, Y9Role.class);

        if (StringUtils.isNotEmpty(y9Role.getId())) {
            Optional<Y9Role> y9RoleOptional = y9RoleManager.findById(y9Role.getId());
            if (y9RoleOptional.isPresent()) {
                Y9Role originRole = PlatformModelConvertUtil.convert(y9RoleOptional.get(), Y9Role.class);
                Y9Role savedRole = y9RoleManager.update(originRole);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.ROLE_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.ROLE_UPDATE.getDescription(), savedRole.getName()))
                    .objectId(savedRole.getId())
                    .oldObject(originRole)
                    .currentObject(savedRole)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return entityToModel(savedRole);
            }
        }

        Y9Role savedRole = y9RoleManager.insert(y9Role);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ROLE_CREATE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ROLE_CREATE.getDescription(), savedRole.getName()))
            .objectId(savedRole.getId())
            .oldObject(null)
            .currentObject(savedRole)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedRole);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void saveOrder(List<String> ids) {
        int index = 0;
        for (String id : ids) {
            Y9Role roleNode = y9RoleManager.getByIdFromCache(id);
            roleNode.setTabIndex(index++);
            y9RoleManager.update(roleNode);
        }
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public Role saveProperties(String id, String properties) {
        Y9Role currentRole = y9RoleManager.getByIdFromCache(id);
        Y9Role originalRole = PlatformModelConvertUtil.convert(currentRole, Y9Role.class);

        currentRole.setProperties(properties);
        Y9Role savedRole = y9RoleManager.update(currentRole);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ROLE_UPDATE_PROPERTIES.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ROLE_UPDATE_PROPERTIES.getDescription(), savedRole.getName(),
                savedRole.getProperties()))
            .objectId(id)
            .oldObject(originalRole)
            .currentObject(savedRole)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        return entityToModel(savedRole);
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public List<Role> treeSearch(String name, String parentId) {
        List<Y9Role> roleList = y9RoleRepository.findByParentIdAndNameContainingOrderByTabIndexAsc(parentId, name);
        Set<Y9Role> roleSet = new HashSet<>(roleList);
        for (Y9Role role : roleList) {
            fillRolesRecursivelyToRoot(role.getParentId(), roleSet);
        }
        return roleSet.stream().sorted().map(Y9RoleServiceImpl::entityToModel).collect(Collectors.toList());
    }

    @Override
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER, readOnly = true)
    public List<Role> treeSearch(String name) {
        List<Y9Role> roleNodeList = y9RoleRepository.findByNameContainingOrderByTabIndexAsc(name);
        Set<Y9Role> roleSet = new HashSet<>(roleNodeList);
        for (Y9Role role : roleNodeList) {
            fillRolesRecursivelyToRoot(role.getParentId(), roleSet);
        }
        return roleSet.stream().sorted().map(Y9RoleServiceImpl::entityToModel).collect(Collectors.toList());
    }

    private void fillRolesRecursivelyToRoot(String parentId, Set<Y9Role> roleSet) {
        if (StringUtils.isEmpty(parentId)) {
            return;
        }
        // parentId 可能为 appId
        Optional<Y9Role> y9RoleOptional = y9RoleManager.findById(parentId);
        if (y9RoleOptional.isPresent()) {
            Y9Role parentNode = y9RoleOptional.get();
            roleSet.add(parentNode);
            fillRolesRecursivelyToRoot(parentNode.getParentId(), roleSet);
        }
    }

    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void recursiveUpdateByDn(Role roleNode) {
        // 调用向下递归改变DN的值
        List<Role> childrenList = listByParentId(roleNode.getId());
        if (!childrenList.isEmpty()) {
            for (Role childrenRole : childrenList) {
                saveOrUpdate(childrenRole);
                recursiveUpdateByDn(childrenRole);
            }
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App y9App = event.getEntity();
        List<Y9Role> y9RoleList = y9RoleRepository.findByAppIdAndParentId(y9App.getId(), y9App.getId());
        for (Y9Role y9Role : y9RoleList) {
            delete(y9Role.getId());
        }
    }

    @EventListener
    @Transactional(value = PUBLIC_TRANSACTION_MANAGER)
    public void onRoleDeleted(Y9EntityDeletedEvent<Y9Role> event) {
        Y9Role y9Role = event.getEntity();
        if (RoleTypeEnum.FOLDER.equals(y9Role.getType())) {
            List<Y9Role> roleNodeList = y9RoleRepository.findByParentIdOrderByTabIndexAsc(y9Role.getId());
            for (Y9Role role : roleNodeList) {
                delete(role.getId());
            }
        }
    }

    private List<Role> entityToModel(List<Y9Role> y9RoleList) {
        return PlatformModelConvertUtil.y9RoleToRole(y9RoleList);
    }
}
