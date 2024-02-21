package net.risesoft.service.relation;

import java.util.List;

import net.risesoft.entity.relation.Y9OrgBasesToRoles;

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
     * @param roleId
     * @param orgIds
     * @param negative
     * @return List<OrgBasesToRoles>
     */
    List<Y9OrgBasesToRoles> addOrgUnitsForRole(String roleId, List<String> orgIds, Boolean negative);

    /**
     * 根据角色id算出传入的组织id列表的关联数量
     *
     * @param roleId
     * @param orgIds
     * @return
     */
    long countByRoleIdAndOrgIds(String roleId, List<String> orgIds);

    /**
     * 根据角色id和机构对象id集合，获取拥有正权限的数量
     *
     * @param roleId 角色id
     * @param orgIds 组织机构对象id集合
     * @return
     */
    long countByRoleIdAndOrgIdsWithoutNegative(String roleId, List<String> orgIds);

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    Y9OrgBasesToRoles getById(String id);

    /**
     * 根据roleId获取映射节点
     *
     * @param roleId
     * @return
     */
    List<Y9OrgBasesToRoles> listByRoleId(String roleId);

    /**
     * 根据roleId获取映射节点
     *
     * @param roleId
     * @param negative 0:正角色关联，1：负角色关联
     * @return
     */
    List<Y9OrgBasesToRoles> listByRoleIdAndNegative(String roleId, Boolean negative);

    /**
     * 根据组织id找角色id列表
     *
     * @param orgId
     * @return
     */
    List<String> listDistinctRoleIdByOrgId(String orgId);

    /**
     * 根据roleID获取组织节点Id
     *
     * @param roleId
     * @return
     */
    List<String> listOrgIdsByRoleId(String roleId);

    /**
     * 根据父id找角色id列表
     *
     * @param parentId
     * @return
     */
    List<String> listRoleIdByParentId(String parentId);

    /**
     * 根据orgId获取角色节点ID
     *
     * @param orgId
     * @param negative 0:正角色关联，1：负角色关联
     * @return
     */
    List<String> listRoleIdsByOrgIdAndNegative(String orgId, Boolean negative);

    /**
     * 根据id移除
     *
     * @param id
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
     * @param roleId
     * @param orgIds
     */
    void removeOrgBases(String roleId, List<String> orgIds);

    /**
     * 对此组织机构节点添加角色关联
     *
     * @param orgId
     * @param roleIds
     * @param negative
     * @return
     */
    List<Y9OrgBasesToRoles> addRolesForOrgUnit(String orgId, List<String> roleIds, Boolean negative);

}
