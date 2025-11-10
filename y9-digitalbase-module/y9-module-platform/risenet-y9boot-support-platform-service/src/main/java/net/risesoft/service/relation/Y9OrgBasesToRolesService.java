package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.model.platform.permission.OrgBasesToRoles;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9OrgBasesToRolesService {

    /**
     * 添加组织机构节点,对此角色的映射
     *
     * @param roleId 角色id
     * @param orgIds 组织机构id列表
     * @param negative 是否为负角色关联，0:正角色关联，1：负角色关联
     * @return {@code List<Y9OrgBasesToRoles>}
     */
    List<OrgBasesToRoles> addOrgUnitsForRole(String roleId, List<String> orgIds, Boolean negative);

    /**
     * 对此组织机构节点添加角色关联
     *
     * @param orgId 组织机构id
     * @param roleIds 角色id列表
     * @param negative 是否为负角色关联，0:正角色关联，1：负角色关联
     * @return {@code List<Y9OrgBasesToRoles>}
     */
    List<OrgBasesToRoles> addRolesForOrgUnit(String orgId, List<String> roleIds, Boolean negative);

    /**
     * 根据roleId获取映射节点
     *
     * @param roleId 角色id
     * @return {@code List<Y9OrgBasesToRoles>}
     */
    List<OrgBasesToRoles> listByRoleId(String roleId);

    /**
     * 根据id移除
     *
     * @param id id
     */
    void remove(String id);

    /**
     * 根据id数组移除
     *
     * @param ids id数组
     */
    void remove(List<String> ids);

    /**
     * 对此角色中移除组织机构节点
     *
     * @param roleId 角色id
     * @param orgIds 组织机构节点列表
     */
    void removeOrgBases(String roleId, List<String> orgIds);

    Y9Page<OrgBasesToRoles> page(Y9PageQuery pageQuery, String roleId, String unitName);

    List<String> listOrgUnitIdByRoleId(String roleId, Boolean negative);
}
