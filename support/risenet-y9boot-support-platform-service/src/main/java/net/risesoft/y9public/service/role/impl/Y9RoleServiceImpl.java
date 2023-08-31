package net.risesoft.y9public.service.role.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.consts.RoleLevelConsts;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.entity.relation.Y9OrgBasesToRoles;
import net.risesoft.enums.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.manager.org.CompositeOrgBaseManager;
import net.risesoft.repository.relation.Y9OrgBasesToRolesRepository;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
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
public class Y9RoleServiceImpl implements Y9RoleService {

    private final JdbcTemplate jdbcTemplate4Public;

    private final Y9RoleRepository y9RoleRepository;
    private final Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository;

    private final CompositeOrgBaseManager compositeOrgBaseManager;
    private final Y9RoleManager y9RoleManager;

    public Y9RoleServiceImpl(@Qualifier("jdbcTemplate4Public") JdbcTemplate jdbcTemplate4Public,
        Y9RoleRepository y9RoleRepository, Y9OrgBasesToRolesRepository y9OrgBasesToRolesRepository,
        CompositeOrgBaseManager compositeOrgBaseManager, Y9RoleManager y9RoleManager) {
        this.jdbcTemplate4Public = jdbcTemplate4Public;
        this.y9RoleRepository = y9RoleRepository;
        this.y9OrgBasesToRolesRepository = y9OrgBasesToRolesRepository;
        this.compositeOrgBaseManager = compositeOrgBaseManager;
        this.y9RoleManager = y9RoleManager;
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role createRole(Y9Role y9Role) {
        Y9Role parent = null;
        if (y9Role.getParentId() != null && StringUtils.isNotEmpty(y9Role.getParentId())) {
            parent = this.findById(y9Role.getParentId());
        }
        if (StringUtils.isBlank(y9Role.getId())) {
            y9Role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (StringUtils.isEmpty(y9Role.getSystemName())) {
            y9Role.setSystemName("");
            y9Role.setSystemCnName("");
        }
        if (y9Role.getTabIndex() == null) {
            Integer maxTabIndex = getMaxTabIndex();
            y9Role.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        }
        if (parent != null) {
            y9Role.setParentId(parent.getId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
            y9Role.setGuidPath(parent.getGuidPath() + "," + y9Role.getId());
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
    public void findAllByParentId(List<Y9Role> y9RoleList, String parentId) {
        List<Y9Role> list = this.listByParentId(parentId);
        for (Y9Role role : list) {
            String parentId2 = role.getId();
            List<Y9Role> list2 = this.listByParentId(parentId2);
            if (!list2.isEmpty()) {
                recursionDown(y9RoleList, parentId2);
            } else {
                y9RoleList.add(role);
            }
        }
    }

    @Override
    public Optional<Y9Role> findByCustomIdAndParentId(String customId, String parentId) {
        return y9RoleRepository.findByCustomIdAndParentId(customId, parentId);
    }

    @Override
    public Y9Role findById(String id) {
        return y9RoleManager.findById(id);
    }

    @Override
    public Y9Role findTopByRoleId(String id) {
        Y9Role role = this.findById(id);
        String parentId = role.getParentId();
        if (parentId != null) {
            role = findTopByRoleId(parentId);
        }
        return role;
    }

    @Override
    public Integer getMaxTabIndex() {
        return y9RoleRepository.findTopByOrderByTabIndexDesc().map(Y9Role::getTabIndex).orElse(0);
    }

    private void getRoleTrees(String roleId, List<Y9Role> roleNodeList) {
        List<Y9Role> childrenList = listByParentId(roleId);
        if (childrenList.isEmpty()) {
            return;
        }
        roleNodeList.addAll(childrenList);
        for (Y9Role roleNode : childrenList) {
            getRoleTrees(roleNode.getId(), roleNodeList);
        }
    }

    @Override
    public List<Y9Role> listAll() {
        return y9RoleRepository.findAll();
    }

    @Override
    public List<Y9Role> listByAppIdAndParentId(String appId, String parentId) {
        return y9RoleManager.listByAppIdAndParentId(appId, parentId);
    }

    @Override
    public List<Y9Role> listByCustomId(String customId) {
        return y9RoleRepository.findByCustomId(customId);
    }

    @Override
    public List<Y9Role> listById(List<String> ids) {
        List<Y9Role> roleNodeList = new ArrayList<>();
        for (String roleId : ids) {
            Y9Role roleNode = this.findById(roleId);
            if (roleNode != null) {
                roleNodeList.add(roleNode);
            }
        }
        List<Y9Role> returnList = new ArrayList<>();
        returnList.addAll(roleNodeList);
        for (Y9Role role : roleNodeList) {
            recursionUpToTop(role.getParentId(), returnList);
        }
        return returnList;
    }

    @Override
    public List<Y9Role> listByName(String name) {
        return y9RoleRepository.findByNameContainingOrderByTabIndexAsc(name);
    }

    @Override
    public List<Y9Role> listByNameAndSystemNameAndPropertiesAndType(String name, String systemName, String properties,
        String type) {
        return y9RoleRepository.findByNameAndSystemNameAndPropertiesAndType(name, systemName, properties, type);
    }

    @Override
    public List<Y9Role> listByNameAndSystemNameAndType(String name, String systemName, String type) {
        return y9RoleRepository.findByNameAndSystemNameAndType(name, systemName, type);
    }

    @Override
    public List<Y9Role> listByOrgUnitId(String orgUnitId) {
        List<String> roleIdList = y9OrgBasesToRolesRepository.findDistinctRoleIdByOrgId(orgUnitId);
        List<Y9Role> roleList = new ArrayList<>();
        for (String roleId : roleIdList) {
            Y9Role role = findById(roleId);
            if (role != null) {
                roleList.add(role);
            }
        }
        return roleList;
    }

    @Override
    public List<Y9Role> listByOrgUnitIdWithoutNegative(String orgUnitId) {
        List<String> roleIdList = y9OrgBasesToRolesRepository.findRoleIdsByOrgIdAndNegative(orgUnitId, Boolean.FALSE);
        List<Y9Role> roleList = new ArrayList<>();
        for (String roleId : roleIdList) {
            Y9Role role = findById(roleId);
            if (role != null) {
                roleList.add(role);
            }
        }
        return roleList;
    }

    @Override
    public List<Y9Role> listByParentId(String parentId) {
        return y9RoleRepository.findByParentIdOrderByTabIndexAsc(parentId);
    }

    @Override
    public List<Y9Role> listByParentIdAndCustomIdAndSystemNameAndType(String parentId, String customId,
        String systemName, String type) {
        return y9RoleRepository.findByParentIdAndCustomIdAndSystemNameAndType(parentId, customId, systemName, type);
    }

    @Override
    public List<Y9Role> listByParentIdAndName(String parentId, String roleName) {
        return y9RoleRepository.findByParentIdAndName(parentId, roleName);
    }

    @Override
    public List<Y9Role> listByParentIdAndSystemNameIn(String parentId, List<String> systemNames) {
        return y9RoleRepository.findByParentIdAndSystemNameInOrderByTabIndexAsc(parentId, systemNames);
    }

    @Override
    public List<Y9Role> listByParentIdAndType(String parentId, String type) {
        return y9RoleRepository.findByParentIdAndType(parentId, type);
    }

    @Override
    public List<Y9Role> listByParentIdIsNull() {
        return y9RoleRepository.findByParentIdIsNullOrderByTabIndexAsc();
    }

    @Override
    public List<Y9Role> listOrgUnitRelated(String orgId) {
        List<Y9Role> y9RoleList = new ArrayList<>();

        // 找到人员到根组织节点及所有中间组织节点关联的角色
        List<String> orgUnitIdList = this.listOrgUnitIdRecursively(orgId);
        Set<String> orgUnitIdSet = new HashSet<>(orgUnitIdList);
        for (String orgUnitId : orgUnitIdSet) {
            List<Y9Role> relatedY9RoleList = this.listByOrgUnitId(orgUnitId);
            if (!relatedY9RoleList.isEmpty()) {
                for (Y9Role y9Role : relatedY9RoleList) {
                    if (!y9RoleList.contains(y9Role)) {
                        y9RoleList.add(y9Role);
                    }
                }
            }
        }
        return y9RoleList;
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
    public List<Y9Person> listRelatedPersonByRoleId(String id) {
        List<Y9Person> y9PersonList = new ArrayList<>();

        List<Y9OrgBasesToRoles> y9OrgBasesToRolesList = y9OrgBasesToRolesRepository.findByRoleId(id);
        for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgBase(y9OrgBasesToRoles.getOrgId());
            if (OrgTypeEnum.PERSON.getEnName().equals(y9OrgBase.getOrgType())) {
                Y9Person person = (Y9Person)y9OrgBase;
                y9PersonList.add(person);
            } else {
                y9PersonList
                    .addAll(compositeOrgBaseManager.listAllPersonsRecursionDownward(y9OrgBasesToRoles.getOrgId()));
            }
        }
        return y9PersonList;
    }

    @Override
    public List<Y9Position> listRelatedPositionByRoleId(String id) {
        List<Y9Position> y9PositionList = new ArrayList<>();
        List<Y9OrgBasesToRoles> y9OrgBasesToRolesList = y9OrgBasesToRolesRepository.findByRoleId(id);
        for (Y9OrgBasesToRoles y9OrgBasesToRoles : y9OrgBasesToRolesList) {
            Y9OrgBase y9OrgBase = compositeOrgBaseManager.getOrgBase(y9OrgBasesToRoles.getOrgId());
            if (OrgTypeEnum.POSITION.getEnName().equals(y9OrgBase.getOrgType())) {
                Y9Position y9Position = (Y9Position)y9OrgBase;
                y9PositionList.add(y9Position);
            } else {
                y9PositionList
                    .addAll(compositeOrgBaseManager.listAllPositionsRecursionDownward(y9OrgBasesToRoles.getOrgId()));
            }
        }
        return y9PositionList;
    }

    @Override
    @Transactional(readOnly = false)
    public void move(String id, String newParentId) {
        Y9Role roleNode = this.findById(id);
        roleNode.setParentId(newParentId);
        saveOrUpdate(roleNode);
        recursiveUpdateByDn(roleNode);
    }

    private void recursionDown(List<Y9Role> lists, String parentId) {
        List<Y9Role> list = this.listByParentId(parentId);
        for (Y9Role role : list) {
            String parentId2 = role.getId();
            List<Y9Role> list2 = this.listByParentId(parentId2);
            if (!list2.isEmpty()) {
                recursionDown(lists, parentId2);
            } else {
                lists.add(role);
            }
        }
    }

    private void recursionUpToTop(String parentId, List<Y9Role> returnList) {
        if (StringUtils.isEmpty(parentId)) {
            return;
        }
        Y9Role parentNode = findById(parentId);
        if (parentNode != null && !returnList.contains(parentNode)) {
            returnList.add(parentNode);
            if (StringUtils.isNotEmpty(parentNode.getParentId())) {
                recursionUpToTop(parentNode.getParentId(), returnList);
            }
        }
    }

    @Transactional(readOnly = false)
    public void recursiveUpdate(Y9Role y9Role) {
        List<Y9Role> childrenList = listByParentId(y9Role.getId());
        if (!childrenList.isEmpty()) {
            for (Y9Role childrenRole : childrenList) {
                Y9Role oldRole = this.findById(childrenRole.getId());
                oldRole.setSystemName(y9Role.getSystemName());
                saveOrUpdate(oldRole);
                recursiveUpdate(oldRole);
            }
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

    @Override
    @Transactional(readOnly = false)
    public void saveOrder(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Y9Role roleNode = this.findById(ids[i]);
            roleNode.setTabIndex(i);
            y9RoleManager.save(roleNode);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role saveOrUpdate(Y9Role y9Role) {
        Y9Role parent = null;
        if (y9Role.getParentId() != null && StringUtils.isNotEmpty(y9Role.getParentId())) {
            parent = this.findById(y9Role.getParentId());
        }
        if (StringUtils.isNotEmpty(y9Role.getId())) {
            Y9Role origRole = this.findById(y9Role.getId());
            if (origRole != null) {
                boolean update = false;
                String systemName = y9Role.getSystemName();
                String oldSystemName = origRole.getSystemName();
                if (StringUtils.isNotBlank(oldSystemName) && !systemName.equals(oldSystemName)) {
                    update = true;
                }

                Y9BeanUtil.copyProperties(y9Role, origRole);
                if (parent != null) {
                    origRole.setParentId(parent.getId());
                    origRole.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
                    origRole.setGuidPath(parent.getGuidPath() + "," + y9Role.getId());
                } else {
                    origRole.setParentId(y9Role.getParentId());
                    origRole.setDn(RoleLevelConsts.CN + y9Role.getName());
                    origRole.setGuidPath(y9Role.getId());
                }
                Y9Role role = y9RoleManager.save(origRole);

                Y9PersonToRoleService y9PersonToRoleService = Y9Context.getBean(Y9PersonToRoleService.class);
                y9PersonToRoleService.updateByRole(role);
                if (update && StringUtils.isNotBlank(y9Role.getParentId())) {
                    recursiveUpdate(role);
                }
                return role;
            }
        }
        y9Role.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        if (StringUtils.isEmpty(y9Role.getSystemName())) {
            y9Role.setSystemName("");
        }
        Integer maxTabIndex = getMaxTabIndex();
        y9Role.setTabIndex(maxTabIndex != null ? maxTabIndex + 1 : 0);
        y9Role.setSystemCnName(y9Role.getSystemCnName());
        if (parent != null) {
            y9Role.setParentId(parent.getId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName() + RoleLevelConsts.SEPARATOR + parent.getDn());
            y9Role.setGuidPath(parent.getGuidPath() + "," + y9Role.getId());
        } else {
            y9Role.setParentId(y9Role.getParentId());
            y9Role.setDn(RoleLevelConsts.CN + y9Role.getName());
            y9Role.setGuidPath(y9Role.getId());
        }
        if (!DefaultIdConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getParentId())) {
            y9Role.setTenantId(Y9LoginUserHolder.getTenantId());
        }
        return y9RoleManager.save(y9Role);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Role saveProperties(String id, String properties) {
        Y9Role roleNode = this.findById(id);
        roleNode.setProperties(properties);
        return y9RoleManager.save(roleNode);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<Y9Role> searchRole(String whereClause) {
        if (StringUtils.isBlank(whereClause)) {
            whereClause = "";
        } else {
            whereClause = " where " + whereClause;
        }
        String sql = "select * from Y9_ROLE " + whereClause;

        return jdbcTemplate4Public.query(sql, new BeanPropertyRowMapper(Y9Role.class));
    }

    @Override
    public List<Y9Role> treeSearchById(String id) {
        List<Y9Role> roleNodeList = new ArrayList<>();
        getRoleTrees(id, roleNodeList);
        return roleNodeList;
    }

    @Override
    public List<Y9Role> treeSearchByName(String name) {
        List<Y9Role> roleNodeList = y9RoleRepository.findByNameContainingOrderByTabIndexAsc(name);
        List<Y9Role> returnList = new ArrayList<>();
        returnList.addAll(roleNodeList);
        for (Y9Role role : roleNodeList) {
            recursionUpToTop(role.getParentId(), returnList);
        }
        return returnList;
    }

    @Override
    public List<Y9Role> treeSearchBySystemName(String name, String systemName) {
        List<Y9Role> roleNodeList =
            y9RoleRepository.findBySystemNameAndNameContainingOrderByTabIndexAsc(systemName, name);
        List<Y9Role> returnList = new ArrayList<>();
        returnList.addAll(roleNodeList);
        for (Y9Role role : roleNodeList) {
            recursionUpToTop(role.getParentId(), returnList);
        }
        return returnList;
    }
}
