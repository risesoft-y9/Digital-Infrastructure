package net.risesoft.api.permission;

import java.util.List;

import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Role;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface RoleApi {

    /**
     * 新增人员到角色
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return bolean 是否增加成功
     * @since 9.6.0
     */
    boolean addPerson(String personId, String roleId, String tenantId);

    /**
     * 新增角色节点（带自定义标示customId）
     *
     * @param roleId 角色id
     * @param roleName 角色名称
     * @param parentId 父节点id
     * @param customId customId对应工作流的processDefineKey
     * @param type 角色类型，systemNode、tenantNode、node或者role
     * @param systemName 系统标识
     * @param systemCnName 系统中文名称
     * @return Role 角色对象
     * @since 9.6.0
     */
    Role createRoleNodeAddCustomId(String roleId, String roleName, String parentId, String customId, String type, String systemName, String systemCnName);

    /**
     * 删除角色（同事删除该角色的授权关系）
     *
     * @param roleId 角色id
     * @return Boolean 是否删除成功
     * @since 9.6.0
     */
    Boolean deleteRole(String roleId);

    /**
     * 根据customId和parentId获取角色
     *
     * @param customId customId
     * @param parentId 角色的父节点id
     * @return Role 角色对象
     * @since 9.6.0
     */
    Role findByCustomIdAndParentId(String customId, String parentId);

    /**
     * 根据id获取相应角色节点
     *
     * @param roleId 角色唯一标识
     * @return Role 角色对象
     * @since 9.6.0
     */
    Role getRole(String roleId);

    /**
     * 根据人员id判断该人员是否拥有roleName这个公共角色
     *
     * @param tenantId 租户id
     * @param roleName 角色名称
     * @param personId 人员id
     * @return boolean
     * @since 9.6.0
     */
    boolean hasPublicRole(String tenantId, String roleName, String personId);

    /**
     * 根据人员id判断该人员是否拥有 roleName 这个角色
     *
     * @param tenantId 租户id
     * @param systemName 系统标识
     * @param properties 角色扩展属性
     * @param roleName 角色名称
     * @param personId 人员id
     * @return Boolean 是否拥有
     * @since 9.6.0
     */
    Boolean hasRole(String tenantId, String systemName, String properties, String roleName, String personId);

    /**
     * 判断orgUnitId是否有角色roleId
     *
     * @param tenantId 租户id
     * @param roleId 角色id
     * @param orgUnitId 组织架构节点id
     * @return Boolean 是否有
     * @since 9.6.0
     */
    Boolean hasRoleByTenantIdAndRoleIdAndOrgUnitId(String tenantId, String roleId, String orgUnitId);

    /**
     * 根据角色Id获取角色下所有人员（递归）
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listAllPersonsById(String tenantId, String roleId);

    /**
     * 根据角色Id获取相应OrgUnits
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @param orgType 组织类型
     * @return List&lt;OrgUnit&gt; 机构对象集合
     * @since 9.6.0
     */
    List<OrgUnit> listOrgUnitsById(String tenantId, String roleId, String orgType);

    /**
     * 根据角色Id获取相应人员
     *
     * @param tenantId 租户id
     * @param roleId 角色唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listPersonsById(String tenantId, String roleId);

    /**
     * 根据人员id获取所有关联的角色
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    List<Role> listRelateRoleByPersonId(String tenantId, String personId);

    /**
     * 根据orgUnitId获取角色节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织架构节点id
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    List<Role> listRoleByOrgUnitId(String tenantId, String orgUnitId);

    /**
     * 根据父节点Id获取相应子级角色节点
     *
     * @param roleId 角色唯一标识
     * @return List&lt;Role&gt; 角色对象集合
     * @since 9.6.0
     */
    List<Role> listRoleByParentId(String roleId);

    /**
     * 删除角色中的人员
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @param tenantId 人员所在的租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    boolean removePerson(String personId, String roleId, String tenantId);
}
