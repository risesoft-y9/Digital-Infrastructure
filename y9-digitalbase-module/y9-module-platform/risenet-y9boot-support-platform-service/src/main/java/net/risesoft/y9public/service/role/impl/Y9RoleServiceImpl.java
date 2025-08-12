package net.risesoft.y9public.service.role.impl;

import java.util.ArrayList;
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
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.role.Y9RoleManager;
import net.risesoft.y9public.repository.role.Y9RoleRepository;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsPublicTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class Y9RoleServiceImpl implements Y9RoleService {

    private final Y9RoleRepository y9RoleRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;

    private final Y9RoleManager y9RoleManager;

    @Transactional(readOnly = false)
    @Override
    public void delete(String id) {
        Y9Role y9Role = y9RoleManager.getById(id);

        y9RoleManager.delete(id);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ROLE_DELETE.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ROLE_DELETE.getDescription(), y9Role.getName()))
            .objectId(id)
            .oldObject(y9Role)
            .currentObject(null)
            .build();
        Y9Context.publishEvent(auditLogEvent);
    }

    @Override
    public Optional<Y9Role> findByCustomIdAndParentId(String customId, String parentId) {
        return y9RoleRepository.findByCustomIdAndParentId(customId, parentId);
    }

    @Override
    public Optional<Y9Role> findById(String id) {
        return y9RoleManager.findById(id);
    }

    @Override
    public Y9Role findTopByRoleId(String id) {
        Y9Role role = y9RoleManager.getById(id);
        String parentId = role.getParentId();
        if (parentId != null) {
            role = findTopByRoleId(parentId);
        }
        return role;
    }

    @Override
    public Y9Role getById(String roleId) {
        return y9RoleManager.getById(roleId);
    }

    @Override
    public List<Y9Role> listAll() {
        return y9RoleRepository.findAll();
    }

    @Override
    public List<Y9Role> listByName(String name) {
        return y9RoleRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    @Override
    public List<Y9Role> listByOrgUnitId(String orgUnitId) {
        List<String> roleIdList = y9OrgBasesToRolesRepository.findDistinctRoleIdByOrgId(orgUnitId);
        List<Y9Role> roleList = new ArrayList<>();
        for (String roleId : roleIdList) {
            Y9Role role = y9RoleManager.getById(roleId);
            roleList.add(role);
        }
        return roleList;
    }

    @Override
    public List<Y9Role> listByOrgUnitIdWithoutNegative(String orgUnitId) {
        List<String> roleIdList = y9OrgBasesToRolesRepository.findRoleIdsByOrgIdAndNegative(orgUnitId, Boolean.FALSE);
        List<Y9Role> roleList = new ArrayList<>();
        for (String roleId : roleIdList) {
            Y9Role role = y9RoleManager.getById(roleId);
            roleList.add(role);
        }
        return roleList;
    }

    @Override
    public List<Y9Role> listByParentId(String parentId) {
        return y9RoleRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    @Override
    public List<Y9Role> listByParentId4Tenant(String parentId, String tenantId) {
        return y9RoleRepository.findByParentIdAndTenantIdOrParentIdAndTenantIdIsNullOrderByTabIndexAsc(parentId,
            tenantId, parentId);
    }

    @Override
    public List<Y9Role> listByParentIdAndName(String parentId, String roleName) {
        return y9RoleRepository.findByParentIdAndName(parentId, roleName);
    }

    @Override
    public List<Y9Role> listByParentIdIsNull() {
        return y9RoleRepository.findByParentIdIsNullOrderByTabIndexAsc();
    }

    @Override
    public List<String> listOrgUnitIdRecursively(String orgUnitId) {
        return y9RoleManager.listOrgUnitIdRecursively(orgUnitId);
    }

    @Override
    public List<Y9Role> listOrgUnitRelatedWithoutNegative(String orgUnitId) {
        return y9RoleManager.listOrgUnitRelatedWithoutNegative(orgUnitId);
    }

    @Override
    @Transactional(readOnly = false)
    public void move(String id, String newParentId) {
        Y9Role currentRole = y9RoleManager.getById(id);
        Y9Role parentToMove = y9RoleManager.getById(newParentId);
        Y9Role originalRole = Y9ModelConvertUtil.convert(currentRole, Y9Role.class);

        currentRole.setParentId(newParentId);
        Y9Role savedRole = y9RoleManager.update(currentRole);

        AuditLogEvent auditLogEvent = AuditLogEvent.builder()
            .action(AuditLogEnum.ROLE_UPDATE_PARENTID.getAction())
            .description(Y9StringUtil.format(AuditLogEnum.ROLE_UPDATE_PARENTID.getDescription(), savedRole.getName(),
                parentToMove.getName()))
            .objectId(id)
            .oldObject(originalRole)
            .currentObject(savedRole)
            .build();
        Y9Context.publishEvent(auditLogEvent);

        recursiveUpdateByDn(savedRole);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role saveOrUpdate(Y9Role y9Role) {
        if (StringUtils.isNotEmpty(y9Role.getId())) {
            Optional<Y9Role> y9RoleOptional = this.findById(y9Role.getId());
            if (y9RoleOptional.isPresent()) {
                Y9Role originRole = Y9ModelConvertUtil.convert(y9RoleOptional.get(), Y9Role.class);
                Y9Role savedRole = y9RoleManager.update(originRole);

                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(AuditLogEnum.ROLE_UPDATE.getAction())
                    .description(Y9StringUtil.format(AuditLogEnum.ROLE_UPDATE.getDescription(), savedRole.getName()))
                    .objectId(savedRole.getId())
                    .oldObject(originRole)
                    .currentObject(savedRole)
                    .build();
                Y9Context.publishEvent(auditLogEvent);

                return savedRole;
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

        return savedRole;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(List<String> ids) {
        int index = 0;
        for (String id : ids) {
            Y9Role roleNode = y9RoleManager.getById(id);
            roleNode.setTabIndex(index++);
            y9RoleManager.update(roleNode);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role saveProperties(String id, String properties) {
        Y9Role currentRole = y9RoleManager.getById(id);
        Y9Role originalRole = Y9ModelConvertUtil.convert(currentRole, Y9Role.class);

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

        return savedRole;
    }

    @Override
    public List<Y9Role> treeSearch(String name, String parentId) {
        List<Y9Role> roleList = y9RoleRepository.findByParentIdAndNameContainingOrderByTabIndexAsc(parentId, name);
        Set<Y9Role> roleSet = new HashSet<>(roleList);
        for (Y9Role role : roleList) {
            fillRolesRecursivelyToRoot(role.getParentId(), roleSet);
        }
        return roleSet.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public List<Y9Role> treeSearch(String name) {
        List<Y9Role> roleNodeList = y9RoleRepository.findByNameContainingOrderByTabIndexAsc(name);
        Set<Y9Role> roleSet = new HashSet<>(roleNodeList);
        for (Y9Role role : roleNodeList) {
            fillRolesRecursivelyToRoot(role.getParentId(), roleSet);
        }
        return roleSet.stream().sorted().collect(Collectors.toList());
    }

    @EventListener
    @Transactional(readOnly = false)
    public void onAppDeleted(Y9EntityDeletedEvent<Y9App> event) {
        Y9App y9App = event.getEntity();
        List<Y9Role> y9RoleList = y9RoleRepository.findByAppIdAndParentId(y9App.getId(), y9App.getId());
        for (Y9Role y9Role : y9RoleList) {
            delete(y9Role.getId());
        }
    }

    private void fillRolesRecursivelyToRoot(String parentId, Set<Y9Role> roleSet) {
        if (StringUtils.isEmpty(parentId)) {
            return;
        }
        // parentId 可能为 appId
        Optional<Y9Role> y9RoleOptional = this.findById(parentId);
        if (y9RoleOptional.isPresent()) {
            Y9Role parentNode = y9RoleOptional.get();
            roleSet.add(parentNode);
            fillRolesRecursivelyToRoot(parentNode.getParentId(), roleSet);
        }
    }

    @Transactional(readOnly = false)
    public void recursiveUpdateByDn(Y9Role roleNode) {
        // 调用向下递归改变DN的值
        List<Y9Role> childrenList = listByParentId(roleNode.getId());
        if (!childrenList.isEmpty()) {
            for (Y9Role childrenRole : childrenList) {
                saveOrUpdate(childrenRole);
                recursiveUpdateByDn(childrenRole);
            }
        }
    }
}
