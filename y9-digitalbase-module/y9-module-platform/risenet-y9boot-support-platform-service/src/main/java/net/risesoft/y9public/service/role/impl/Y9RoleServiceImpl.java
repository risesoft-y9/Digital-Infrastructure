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

import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.RoleLevelConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.event.Y9EntityDeletedEvent;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.manager.resource.Y9AppManager;
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
    private final Y9AppManager y9AppManager;

    @Override
    @Transactional(readOnly = false)
    public Y9Role createRole(Y9Role y9Role) {
        if (StringUtils.isBlank(y9Role.getId())) {
            y9Role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (y9Role.getTabIndex() == null) {
            y9Role.setTabIndex(getNextTabIndex());
        }
        Optional<Y9Role> parentRoleOptional = Optional.empty();
        if (StringUtils.isNotEmpty(y9Role.getParentId())) {
            parentRoleOptional = this.findById(y9Role.getParentId());
        }
        if (parentRoleOptional.isPresent()) {
            Y9Role parent = parentRoleOptional.get();
            y9Role.setParentId(parent.getId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
            y9Role.setGuidPath(parent.getGuidPath() + RoleLevelConsts.SEPARATOR + y9Role.getId());
        } else {
            y9Role.setParentId(y9Role.getParentId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName());
            y9Role.setGuidPath(y9Role.getId());
        }
        return y9RoleManager.save(y9Role);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(String id) {
        y9RoleManager.delete(id);
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

    private Integer getNextTabIndex() {
        return y9RoleRepository.findTopByOrderByTabIndexDesc().map(Y9Role::getTabIndex).orElse(-1) + 1;
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
        Y9Role roleNode = y9RoleManager.getById(id);
        roleNode.setParentId(newParentId);
        saveOrUpdate(roleNode);
        recursiveUpdateByDn(roleNode);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role saveOrUpdate(Y9Role y9Role) {
        Y9Role parent = null;
        if (StringUtils.isNotEmpty(y9Role.getParentId())) {
            parent = this.findById(y9Role.getParentId()).orElse(null);
        }
        if (StringUtils.isNotEmpty(y9Role.getId())) {
            Optional<Y9Role> y9RoleOptional = this.findById(y9Role.getId());
            if (y9RoleOptional.isPresent()) {
                Y9Role originRole = y9RoleOptional.get();

                Y9BeanUtil.copyProperties(y9Role, originRole);
                if (parent != null) {
                    originRole.setParentId(parent.getId());
                    originRole
                            .setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
                    originRole.setGuidPath(parent.getGuidPath() + RoleLevelConsts.SEPARATOR + y9Role.getId());
                } else {
                    originRole.setParentId(y9Role.getParentId());
                    originRole.setDn(RoleLevelConsts.CN + y9Role.getName());
                    originRole.setGuidPath(y9Role.getId());
                }
                if (StringUtils.isBlank(originRole.getAppId())) {
                    originRole.setAppId(null);
                }
                if (StringUtils.isBlank(originRole.getSystemId())) {
                    originRole.setSystemId(null);
                }
                return y9RoleManager.save(originRole);
            }
        }
        y9Role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9Role.setTabIndex(getNextTabIndex());
        if (parent != null) {
            y9Role.setParentId(parent.getId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
            y9Role.setGuidPath(parent.getGuidPath() + RoleLevelConsts.SEPARATOR + y9Role.getId());
        } else {
            y9Role.setParentId(y9Role.getParentId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName());
            y9Role.setGuidPath(y9Role.getId());
        }
        if (StringUtils.isNotBlank(y9Role.getAppId())) {
            Y9App y9App = y9AppManager.getById(y9Role.getAppId());
            y9Role.setSystemId(y9App.getSystemId());
        }

        if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getParentId())) {
            y9Role.setTenantId(Y9LoginUserHolder.getTenantId());
        }
        if (InitDataConsts.OPERATION_TENANT_ID.equals(Y9LoginUserHolder.getTenantId())) {
            y9Role.setTenantId(null);
        }
        return y9RoleManager.save(y9Role);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(List<String> ids) {
        int index = 0;
        for (String id : ids) {
            Y9Role roleNode = y9RoleManager.getById(id);
            roleNode.setTabIndex(index++);
            y9RoleManager.save(roleNode);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role saveProperties(String id, String properties) {
        Y9Role roleNode = y9RoleManager.getById(id);
        roleNode.setProperties(properties);
        return y9RoleManager.save(roleNode);
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
