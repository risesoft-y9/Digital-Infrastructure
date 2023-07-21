package net.risesoft.service.identity.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.CacheNameConsts;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.manager.authorization.Y9PersonToRoleManager;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.identity.person.Y9PersonToRoleRepository;
import net.risesoft.service.identity.Y9PersonToRoleService;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.repository.role.Y9RoleRepository;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@CacheConfig(cacheNames = CacheNameConsts.PERSONS_TO_ROLES)
@Service
@Slf4j
@RequiredArgsConstructor
public class Y9PersonToRoleServiceImpl implements Y9PersonToRoleService {

    private final Y9PersonToRoleRepository y9PersonToRoleRepository;
    private final Y9RoleRepository y9RoleRepository;
    private final Y9PersonToRoleManager y9PersonToRoleManager;
    
    @Override
    public long countByPersonId(String personId) {
        return y9PersonToRoleRepository.countByPersonId(personId);
    }

    @Override
    public long countByPersonIdAndSystemName(String personId, String systemName) {
        return y9PersonToRoleRepository.countByPersonIdAndSystemName(personId, systemName);
    }
    
    private Page<Y9Role> findByType(String type, int page, int rows) {
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByType(type, pageable);
    }

    private void getIds(List<String> ids, List<String> appNames) {
        Y9Role y9Role = y9RoleRepository.findByDnAndType("cn=协同办公", "node");
        if (y9Role != null) {
            List<Y9Role> roleList = y9RoleRepository.findByParentIdOrderByTabIndexAsc(y9Role.getId());
            for (Y9Role roleNode : roleList) {
                String name = roleNode.getName();
                if ("党代表".equals(name) || "全文检索".equals(name) || "统计分析".equals(name) || "人大工作".equals(name) || "区长业务".equals(name) || "区长办件".equals(name)) {
                    ids.add(roleNode.getId());
                    appNames.add(name);
                }
            }
        }
    }

    private Y9Page<Map<String, Object>> getPersonRoleAppMatrixList(List<String> roleIds, Page<Y9PersonToRole> personRoleMappingList, int page) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            for (Y9PersonToRole role : personRoleMappingList) {
                String access = "√";
                Map<String, Object> map = new HashMap<>(16);
                map.put("systemCnName", role.getSystemCnName());
                map.put("appName", role.getAppName());
                map.put("roleName", role.getRoleName());
                map.put("description", role.getDescription());
                map.put("access", access);
                list.add(map);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Page.success(page, personRoleMappingList.getTotalPages(), personRoleMappingList.getTotalElements(), list);
    }

    private Y9Page<Map<String, Object>> getY9PageAppY9Role(List<String> roleIds, Page<Y9Role> roleNodesList, String appName, int page) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            for (Y9Role role : roleNodesList) {
                String access = "";
                Map<String, Object> map = new HashMap<>(16);
                map.put("systemCnName", role.getSystemCnName());
                map.put("appName", appName);
                map.put("roleName", role.getName());
                map.put("description", role.getDescription());
                if (roleIds.contains(role.getId())) {
                    access = "√";
                } else {
                    access = "";
                }
                map.put("access", access);
                list.add(map);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return Y9Page.success(page, roleNodesList.getTotalPages(), roleNodesList.getTotalElements(), list);
    }

    private Y9Page<Map<String, Object>> getY9PageAppY9Role2(List<String> roleIds, Page<Y9Role> roleNodesList, String appName, int page) {
        List<Map<String, Object>> list = new ArrayList<>();
        int count = 0;
        try {
            for (Y9Role role : roleNodesList) {
                String access = "";
                Map<String, Object> map = new HashMap<>(16);
                if (StringUtils.isNotBlank(role.getParentId())) {
                    Y9Role roleNode = y9RoleRepository.findById(role.getParentId()).orElse(null);
                    if (roleNode != null && appName.equals(roleNode.getName())) {
                        map.put("appName", roleNode.getName());
                        map.put("systemCnName", role.getSystemCnName());
                        map.put("roleName", role.getName());
                        map.put("description", role.getDescription());
                        if (roleIds.contains(role.getId())) {
                            access = "√";
                        } else {
                            access = "";
                        }
                        map.put("access", access);
                        list.add(map);
                        count = count + 1;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Page.success(page, roleNodesList.getTotalPages(), count, list);
    }

    private Y9Page<Map<String, Object>> getY9PageAppY9Role3(List<String> roleIds, Page<Y9Role> roleNodesList, String appName, int page) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            for (Y9Role role : roleNodesList) {
                String access = "";
                Map<String, Object> map = new HashMap<>(16);
                map.put("systemCnName", role.getSystemCnName());
                if (StringUtils.isNotBlank(role.getParentId())) {
                    Y9Role roleNode = y9RoleRepository.findById(role.getParentId()).orElse(null);
                    if (roleNode != null) {
                        map.put("appName", roleNode.getName());
                    } else {
                        map.put("appName", role.getSystemCnName());
                    }
                }
                map.put("roleName", role.getName());
                map.put("description", role.getDescription());
                if (roleIds.contains(role.getId())) {
                    access = "√";
                } else {
                    access = "";
                }
                map.put("access", access);
                list.add(map);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Page.success(page, roleNodesList.getTotalPages(), roleNodesList.getTotalElements(), list);
    }

    private Y9Page<Map<String, Object>> getY9PageY9Role(List<String> roleIds, Page<Y9Role> roleNodesList, int page) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            for (Y9Role role : roleNodesList) {
                String access = "";
                Map<String, Object> map = new HashMap<>(16);
                map.put("systemCnName", role.getSystemCnName());
                if (StringUtils.isNotBlank(role.getParentId())) {
                    Y9Role roleNode = y9RoleRepository.findById(role.getParentId()).orElse(null);
                    if (roleNode != null) {
                        map.put("appName", roleNode.getName());
                    } else {
                        map.put("appName", role.getSystemCnName());
                    }
                }
                map.put("roleName", role.getName());
                map.put("description", role.getDescription());
                if (roleIds.contains(role.getId())) {
                    access = "√";
                } else {
                    access = "";
                }
                map.put("access", access);
                list.add(map);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Page.success(page, roleNodesList.getTotalPages(), roleNodesList.getTotalElements(), list);
    }

    private Y9Page<Map<String, Object>> getY9RoleList(List<String> roleIds, Page<Y9Role> roleNodesList, String appName, int page) {
        List<Map<String, Object>> list = new ArrayList<>();
        int count = 0;
        try {
            for (Y9Role role : roleNodesList) {
                String access = "";
                Map<String, Object> map = new HashMap<>(16);
                if (StringUtils.isNotBlank(role.getParentId())) {
                    Y9Role roleNode = y9RoleRepository.findById(role.getParentId()).orElse(null);
                    if (roleNode != null && appName.equals(roleNode.getName())) {
                        map.put("appName", roleNode.getName());
                        map.put("systemCnName", role.getSystemCnName());
                        map.put("roleName", role.getName());
                        map.put("description", role.getDescription());
                        if (roleIds.contains(role.getId())) {
                            access = "√";
                        } else {
                            access = "";
                        }
                        map.put("access", access);
                        count = count + 1;
                        list.add(map);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Y9Page.success(page, 1, count, list);
    }

    @Override
    public Boolean hasRole(String personId, String customId) {
        int count = y9PersonToRoleRepository.countByPersonIdAndRoleCustomId(personId, customId);
        return count > 0;
    }

    @Override
    public List<Y9PersonToRole> listByPersonId(String personId) {
        return y9PersonToRoleRepository.findByPersonId(personId);
    }

    private Page<Y9Role> pageByNameAndSystemCnNameAndType(String name, String systemCnName, String type, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByNameAndSystemCnNameAndType(name, systemCnName, type, pageable);
    }

    private Page<Y9Role> pageByNameAndSystemCnNameAndTypeAndParentIdNotIn(String name, String systemCnName, String type, List<String> ids, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByNameAndSystemCnNameAndTypeAndParentIdNotIn(name, systemCnName, type, ids, pageable);
    }

    private Page<Y9Role> pageByNameAndType(String name, String type, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByNameAndType(name, type, pageable);
    }

    private Page<Y9Role> pageByNameAndTypeAndParentIdNotIn(String name, String type, List<String> ids, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByNameAndTypeAndParentIdNotIn(name, type, ids, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonId(String personId, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonId(personId, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndAppName(String personId, String appName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "systemCnName"));
        return y9PersonToRoleRepository.findByPersonIdAndAppName(personId, appName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndAppNameAndSystemCnName(String personId, String appName, String systemCnName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "systemCnName"));
        return y9PersonToRoleRepository.findByPersonIdAndAppNameAndSystemCnName(personId, appName, systemCnName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndAppNameAndSystemCnNameAndRoleName(String personId, String appName, String systemCnName, String roleName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndAppNameAndSystemCnNameAndRoleName(personId, appName, systemCnName, roleName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndAppNames(String personId, List<String> appNames, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndAppNameNotIn(personId, appNames, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndRoleName(String personId, String roleName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndRoleName(personId, roleName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndRoleNameAndAppName(String personId, String roleName, String appName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndRoleNameAndAppName(personId, roleName, appName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndRoleNameAndSystemCnName(String personId, String roleName, String systemCnName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "systemCnName"));
        return y9PersonToRoleRepository.findByPersonIdAndSystemCnNameAndRoleName(personId, systemCnName, roleName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndRoleNameAndSystemCnNames(String personId, String roleName, String systemCnName, List<String> appNames, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndRoleNameAndSystemCnNameAndAppNameNotIn(personId, roleName, systemCnName, appNames, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndRoleNames(String personId, String roleName, List<String> appNames, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndRoleNameAndAppNameNotIn(personId, roleName, appNames, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndSystemCnName(String personId, String systemCnName, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndSystemCnName(personId, systemCnName, pageable);
    }

    @Override
    public Page<Y9PersonToRole> pageByPersonIdAndSystemCnNames(String personId, String systemCnName, List<String> appNames, int page, int rows) {
        if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "appName"));
        return y9PersonToRoleRepository.findByPersonIdAndSystemCnNameAndAppNameNotIn(personId, systemCnName, appNames, pageable);
    }

    private Page<Y9Role> pageByRoleAndTypeAndDnLike(String roleName, String type, String dn, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByNameAndTypeAndDnContaining(roleName, type, dn, pageable);
    }

    private Page<Y9Role> pageBySystemCnNameAndType(String systemCnName, String type, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findBySystemCnNameAndType(systemCnName, type, pageable);
    }

    private Page<Y9Role> pageBySystemCnNameAndTypeAndDnLike(String systemCnName, String type, String dn, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findBySystemCnNameAndTypeAndDnContaining(systemCnName, type, dn, pageable);
    }

    private Page<Y9Role> pageBySystemCnNameAndTypeAndParentId(String systemCnName, String type, String parentId, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findBySystemCnNameAndTypeAndParentId(systemCnName, type, parentId, pageable);
    }

    private Page<Y9Role> pageBySystemCnNameAndTypeAndParentIdNotIn(String systemCnName, String type, List<String> ids, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findBySystemCnNameAndTypeAndParentIdNotIn(systemCnName, type, ids, pageable);
    }

    private Page<Y9Role> pageByTypeAndDnLike(String type, String dn, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByTypeAndDnContaining(type, dn, pageable);
    }

    private Page<Y9Role> pageByTypeAndParentId(String type, String parentId, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByTypeAndParentId(type, parentId, pageable);
    }

    private Page<Y9Role> pageByTypeAndParentIdNotIn(String type, List<String> ids, int page, int rows) {
        page = page < 1 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(page, rows, Sort.by(Sort.Direction.ASC, "systemCnName", "tabIndex"));
        return y9RoleRepository.findByTypeAndParentIdNotIn(type, ids, pageable);
    }

    @Override
    public Y9Page<Map<String, Object>> pagePersonAccessPermission(String personId, String type, String systemCnName, String appName, String roleName, int page, int rows) {
        Y9Page<Map<String, Object>> result = Y9Page.success(page, 0, 0, new ArrayList<>());
        List<Y9Role> y9RoleList = y9RoleRepository.findByNameAndSystemNameAndType("权限查看", "risePortal", "role");
        Y9Role acr = null;
        if (null != y9RoleList && !y9RoleList.isEmpty()) {
            acr = y9RoleList.get(0);
        }
        List<String> ids = new ArrayList<>();
        List<String> appNames = new ArrayList<>();
        this.getIds(ids, appNames);
        // 获取个人拥有的权限id
        List<String> roleIds = y9PersonToRoleManager.listRoleIdsByPersonId(personId);
        // 查所有
        if (StringUtils.isBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if ("0".equals(type) || StringUtils.isBlank(type)) {
                try {
                    if (acr != null && roleIds.contains(acr.getId())) {
                        Page<Y9Role> roleNodesList = this.findByType("role", page, rows);

                        result = getY9PageY9Role(roleIds, roleNodesList, page);
                    } else {
                        if (!ids.isEmpty()) {
                            Page<Y9Role> roleNodesList = this.pageByTypeAndParentIdNotIn("role", ids, page, rows);
                            result = getY9PageY9Role(roleIds, roleNodesList, page);
                        } else {
                            Page<Y9Role> roleNodesList = this.findByType("role", page, rows);
                            result = getY9PageY9Role(roleIds, roleNodesList, page);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if ("1".equals(type)) {
                if (acr != null && roleIds.contains(acr.getId())) {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonId(personId, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                } else {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNames(personId, appNames, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                }
            }
        }

        // 查系统、角色、应用
        if (StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if ("0".equals(type)) {
                try {
                    if (acr != null && roleIds.contains(acr.getId())) {
                        Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndType(roleName, systemCnName, "role", page, 50);
                        result = getY9RoleList(roleIds, roleNodesList, appName, page);
                    } else {
                        if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                            Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndType(roleName, systemCnName, "role", page, 50);
                            result = getY9RoleList(roleIds, roleNodesList, appName, page);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }

            }
            if ("1".equals(type)) {
                if (acr != null && roleIds.contains(acr.getId())) {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNameAndSystemCnNameAndRoleName(personId, appName, systemCnName, roleName, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                } else {
                    if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                        Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNameAndSystemCnNameAndRoleName(personId, appName, systemCnName, roleName, page, rows);
                        result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                    } else {
                        result = Y9Page.success(page, 0, 0, new ArrayList<>());
                    }
                }
            }
        }

        // 查系统
        if (StringUtils.isBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if ("0".equals(type)) {
                if (acr != null && roleIds.contains(acr.getId())) {
                    Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndType(systemCnName, "role", page, rows);
                    result = getY9PageY9Role(roleIds, roleNodesList, page);
                } else {
                    if (ids != null && !ids.isEmpty()) {
                        Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndParentIdNotIn(systemCnName, "role", ids, page, rows);
                        result = getY9PageY9Role(roleIds, roleNodesList, page);
                    } else {
                        Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndType(systemCnName, "role", page, rows);
                        result = getY9PageY9Role(roleIds, roleNodesList, page);
                    }
                }
            }
            if ("1".equals(type)) {
                if (acr != null && roleIds.contains(acr.getId())) {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndSystemCnName(personId, systemCnName, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                } else {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndSystemCnNames(personId, systemCnName, appNames, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                }
            }
        }

        // 查应用
        if (StringUtils.isBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if ("0".equals(type)) {
                if (acr != null && roleIds.contains(acr.getId())) {
                    String dn1 = "cn=" + appName;
                    Y9Role y9Role = y9RoleRepository.findByDnAndType(dn1, "node");
                    if (y9Role == null) {
                        String dn = ",cn=" + appName + ",cn=";
                        Page<Y9Role> roleNodesList = this.pageByTypeAndDnLike("role", dn, page, rows);
                        result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                    } else {
                        String id = y9Role.getId();
                        String dn2 = "cn=" + appName + ",cn=" + appName;
                        Y9Role acRole = y9RoleRepository.findByDnAndType(dn2, "node");
                        if (acRole == null) {
                            Page<Y9Role> roleNodesList = this.pageByTypeAndParentId("role", id, page, rows);
                            result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                        } else {
                            Page<Y9Role> roleNodesList = this.pageByTypeAndDnLike("role", dn1, page, rows);
                            result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                        }
                    }
                } else {
                    if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                        String dn1 = "cn=" + appName;
                        Y9Role y9Role = y9RoleRepository.findByDnAndType(dn1, "node");
                        if (y9Role == null) {
                            String dn = ",cn=" + appName + ",cn=";
                            Page<Y9Role> roleNodesList = this.pageByTypeAndDnLike("role", dn, page, rows);
                            result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                        } else {
                            String id = y9Role.getId();
                            String dn2 = "cn=" + appName + ",cn=" + appName;
                            Y9Role acRole = y9RoleRepository.findByDnAndType(dn2, "node");
                            if (acRole == null) {
                                Page<Y9Role> roleNodesList = this.pageByTypeAndParentId("role", id, page, rows);
                                result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                            } else {
                                Page<Y9Role> roleNodesList = this.pageByTypeAndDnLike("role", dn1, page, rows);
                                result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                            }
                        }
                    } else {
                        result = Y9Page.success(page, 0, 0, new ArrayList<>());
                    }
                }
            }
            if ("1".equals(type)) {
                if (acr != null && roleIds.contains(acr.getId())) {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppName(personId, appName, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                } else {
                    if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                        Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppName(personId, appName, page, rows);
                        result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                    } else {
                        result = Y9Page.success(page, 0, 0, new ArrayList<>());
                    }
                }
            }
        }

        // 查角色
        if ("0".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                Page<Y9Role> roleNodesList = this.pageByNameAndType(roleName, "role", page, rows);
                result = getY9PageAppY9Role3(roleIds, roleNodesList, appName, page);
            } else {
                if (ids != null && !ids.isEmpty()) {
                    Page<Y9Role> roleNodesList = this.pageByNameAndTypeAndParentIdNotIn(roleName, "role", ids, page, rows);
                    result = getY9PageAppY9Role3(roleIds, roleNodesList, appName, page);
                } else {
                    Page<Y9Role> roleNodesList = this.pageByNameAndType(roleName, "role", page, rows);
                    result = getY9PageAppY9Role3(roleIds, roleNodesList, appName, page);
                }
            }
        }
        if ("1".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleName(personId, roleName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            } else {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNames(personId, roleName, appNames, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            }
        }
        // 查角色和系统
        if ("0".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndType(roleName, systemCnName, "role", page, rows);
                result = getY9PageAppY9Role3(roleIds, roleNodesList, appName, page);
            } else {
                if (ids != null && !ids.isEmpty()) {
                    Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndTypeAndParentIdNotIn(roleName, systemCnName, "role", ids, page, rows);
                    result = getY9PageAppY9Role3(roleIds, roleNodesList, appName, page);
                } else {
                    Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndType(roleName, systemCnName, "role", page, rows);
                    result = getY9PageAppY9Role3(roleIds, roleNodesList, appName, page);
                }
            }
        }
        if ("1".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNameAndSystemCnName(personId, roleName, systemCnName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            } else {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNameAndSystemCnNames(personId, roleName, systemCnName, appNames, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            }
        }
        // 查应用和系统
        if ("0".equals(type) && StringUtils.isBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                if (systemCnName.equals(appName)) {
                    String dn = "cn=" + appName + ",cn=" + systemCnName;
                    Y9Role y9Role = y9RoleRepository.findByDnAndType(dn, "node");
                    if (y9Role == null) {
                        Y9Role acRole = y9RoleRepository.findByDnAndType("cn=" + systemCnName, "node");
                        Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndParentId(systemCnName, "role", acRole.getId(), page, rows);
                        result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                    } else {
                        String dn1 = "cn=" + appName;
                        Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndDnLike(systemCnName, "role", dn1, page, rows);
                        result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                    }
                } else {
                    String dn = ",cn=" + appName + ",cn=";
                    Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndDnLike(systemCnName, "role", dn, page, rows);
                    result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                }
            } else {
                if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                    if (systemCnName.equals(appName)) {
                        String dn = "cn=" + appName + ",cn=" + systemCnName;
                        Y9Role y9Role = y9RoleRepository.findByDnAndType(dn, "node");
                        if (y9Role == null) {
                            Y9Role acRole = y9RoleRepository.findByDnAndType("cn=" + systemCnName, "node");
                            Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndParentId(systemCnName, "role", acRole.getId(), page, rows);
                            result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                        } else {
                            String dn1 = "cn=" + appName;
                            Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndDnLike(systemCnName, "role", dn1, page, rows);
                            result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                        }
                    } else {
                        String dn = ",cn=" + appName + ",cn=";
                        Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndDnLike(systemCnName, "role", dn, page, rows);
                        result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                    }
                } else {
                    result = Y9Page.success(page, 0, 0, new ArrayList<>());
                }
            }
        }
        if ("1".equals(type) && StringUtils.isBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNameAndSystemCnName(personId, appName, systemCnName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            } else {
                if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNameAndSystemCnName(personId, appName, systemCnName, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                } else {
                    result = Y9Page.success(page, 0, 0, new ArrayList<>());
                }
            }
        }
        // 查应用和角色
        if ("0".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                String dn = "cn=" + roleName + ",cn=" + appName;
                Page<Y9Role> roleNodesList = this.pageByRoleAndTypeAndDnLike(roleName, "role", dn, page, rows);
                result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
            } else {
                if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                    String dn = "cn=" + roleName + ",cn=" + appName;
                    Page<Y9Role> roleNodesList = this.pageByRoleAndTypeAndDnLike(roleName, "role", dn, page, rows);
                    result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                } else {
                    result = Y9Page.success(page, 0, 0, new ArrayList<>());
                }
            }
        }
        if ("1".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if (acr != null && roleIds.contains(acr.getId())) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNameAndAppName(personId, roleName, appName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            } else {
                if (!"党代表".equals(appName) && !"全文检索".equals(appName) && !"统计分析".equals(appName) && !"人大工作".equals(appName) && !"区长业务".equals(appName) && !"区长办件".equals(appName)) {
                    Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNameAndAppName(personId, roleName, appName, page, rows);
                    result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
                } else {
                    result = Y9Page.success(page, 0, 0, new ArrayList<>());
                }
            }
        }
        return result;
    }

    @Override
    public Y9Page<Map<String, Object>> pagePersonPermission(String personId, String type, String systemCnName, String appName, String roleName, int page, int rows) {
        Y9Page<Map<String, Object>> result = Y9Page.success(page, 0, 0, new ArrayList<>());
        // 获取个人拥有的权限id
        List<String> roleIds = y9PersonToRoleManager.listRoleIdsByPersonId(personId);
        // 查所有
        if (StringUtils.isBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if ("0".equals(type) || StringUtils.isBlank(type)) {
                try {
                    Page<Y9Role> roleNodesList = this.findByType("role", page, rows);
                    result = getY9PageY9Role(roleIds, roleNodesList, page);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if ("1".equals(type)) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonId(personId, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            }
        }

        // 查系统、角色、应用
        if (StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if ("0".equals(type)) {
                try {
                    Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndType(roleName, systemCnName, "role", page, 50);
                    result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
            if ("1".equals(type)) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNameAndSystemCnNameAndRoleName(personId, appName, systemCnName, roleName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            }
        }

        // 查系统
        if (StringUtils.isBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isBlank(appName)) {
            if ("0".equals(type)) {
                Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndType(systemCnName, "role", page, rows);
                result = getY9PageY9Role(roleIds, roleNodesList, page);
            }
            if ("1".equals(type)) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndSystemCnName(personId, systemCnName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            }
        }

        // 查应用
        if (StringUtils.isBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if ("0".equals(type)) {
                String dn1 = "cn=" + appName;
                Y9Role y9Role = y9RoleRepository.findByDnAndType(dn1, "node");
                if (y9Role == null) {
                    String dn = ",cn=" + appName + ",cn=";
                    Page<Y9Role> roleNodesList = this.pageByTypeAndDnLike("role", dn, page, rows);
                    result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                } else {
                    String id = y9Role.getId();
                    String dn2 = "cn=" + appName + ",cn=" + appName;
                    Y9Role acRole = y9RoleRepository.findByDnAndType(dn2, "node");
                    if (acRole == null) {
                        Page<Y9Role> roleNodesList = this.pageByTypeAndParentId("role", id, page, rows);
                        result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                    } else {
                        Page<Y9Role> roleNodesList = this.pageByTypeAndDnLike("role", dn1, page, rows);
                        result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                    }
                }
            }
            if ("1".equals(type)) {
                Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppName(personId, appName, page, rows);
                result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
            }
        }

        // 查角色
        if ("0".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isBlank(appName)) {
            Page<Y9Role> roleNodesList = this.pageByNameAndType(roleName, "role", page, rows);
            result = getY9PageY9Role(roleIds, roleNodesList, page);
        }
        if ("1".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isBlank(appName)) {
            Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleName(personId, roleName, page, rows);
            result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
        }
        // 查角色和系统
        if ("0".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isBlank(appName)) {
            Page<Y9Role> roleNodesList = this.pageByNameAndSystemCnNameAndType(roleName, systemCnName, "role", page, rows);
            result = getY9PageY9Role(roleIds, roleNodesList, page);
        }
        if ("1".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isBlank(appName)) {
            Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNameAndSystemCnName(personId, roleName, systemCnName, page, rows);
            result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
        }
        // 查应用和系统
        if ("0".equals(type) && StringUtils.isBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            if (systemCnName.equals(appName)) {
                String dn = "cn=" + appName + ",cn=" + systemCnName;
                Y9Role y9Role = y9RoleRepository.findByDnAndType(dn, "node");
                if (y9Role == null) {
                    Y9Role acRole = y9RoleRepository.findByDnAndType("cn=" + systemCnName, "node");
                    Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndParentId(systemCnName, "role", acRole.getId(), page, rows);
                    result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
                } else {
                    String dn1 = "cn=" + appName;
                    Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndDnLike(systemCnName, "role", dn1, page, rows);
                    result = getY9PageAppY9Role2(roleIds, roleNodesList, appName, page);
                }
            } else {
                String dn = ",cn=" + appName + ",cn=";
                Page<Y9Role> roleNodesList = this.pageBySystemCnNameAndTypeAndDnLike(systemCnName, "role", dn, page, rows);
                result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
            }
        }
        if ("1".equals(type) && StringUtils.isBlank(roleName) && StringUtils.isNotBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndAppNameAndSystemCnName(personId, appName, systemCnName, page, rows);
            result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
        }
        // 查应用和角色
        if ("0".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            String dn = "cn=" + roleName + ",cn=" + appName;
            Page<Y9Role> roleNodesList = this.pageByRoleAndTypeAndDnLike(roleName, "role", dn, page, rows);
            result = getY9PageAppY9Role(roleIds, roleNodesList, appName, page);
        }
        if ("1".equals(type) && StringUtils.isNotBlank(roleName) && StringUtils.isBlank(systemCnName) && StringUtils.isNotBlank(appName)) {
            Page<Y9PersonToRole> personRoleMappingList = this.pageByPersonIdAndRoleNameAndAppName(personId, roleName, appName, page, rows);
            result = getPersonRoleAppMatrixList(roleIds, personRoleMappingList, page);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void removeByPersonId(String personId) {
        List<Y9PersonToRole> y9PersonToRoleList = y9PersonToRoleRepository.findByPersonId(personId);
        y9PersonToRoleRepository.deleteAll(y9PersonToRoleList);
    }
    
    @Override
    @Transactional(readOnly = false)
    public void removeByRoleId(String roleId) {
        List<Y9PersonToRole> mappingList = y9PersonToRoleRepository.findByRoleId(roleId);
        y9PersonToRoleRepository.deleteAll(mappingList);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(String roleId, String roleName, String systemName, String systemCnName, String description) {
        List<Y9PersonToRole> list = y9PersonToRoleRepository.findByRoleId(roleId);
        List<Integer> ids = list.stream().map(Y9PersonToRole::getId).collect(Collectors.toList());
        for (Integer id : ids) {
            Y9PersonToRole personRoleMapping = y9PersonToRoleRepository.findById(id).orElse(null);
            personRoleMapping.setRoleName(roleName);
            personRoleMapping.setSystemCnName(systemCnName);
            personRoleMapping.setSystemName(systemName);
            personRoleMapping.setDescription(description);
            y9PersonToRoleRepository.save(personRoleMapping);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateByRole(Y9Role y9Role) {
        List<Y9PersonToRole> mappingList = y9PersonToRoleRepository.findByRoleId(y9Role.getId());
        for (Y9PersonToRole positionToRole : mappingList) {
            positionToRole.setRoleName(y9Role.getName());
            positionToRole.setSystemName(y9Role.getSystemName());
            positionToRole.setSystemCnName(y9Role.getSystemCnName());
            positionToRole.setDescription(y9Role.getDescription());
            y9PersonToRoleRepository.save(positionToRole);
        }
    }
}
