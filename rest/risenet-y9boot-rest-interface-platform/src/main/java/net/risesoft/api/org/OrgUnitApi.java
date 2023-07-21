package net.risesoft.api.org;

import java.util.List;

import net.risesoft.model.Department;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;

/**
 * 组织节点组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface OrgUnitApi {

    /**
     * 根据租户id和节点id获取委办局
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    OrgUnit getBureau(String tenantId, String orgUnitId);

    /**
     * 获得部门树
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识(可能是机构ID,也可能是部门ID)
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> getDeptTrees(String tenantId, String orgUnitId);

    /**
     * 获取组织节点所在的组织机构
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return Organization 机构对象
     * @since 9.6.0
     */
    Organization getOrganization(String tenantId, String orgUnitId);

    /**
     * 根据id获得机构对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    OrgUnit getOrgUnit(String tenantId, String orgUnitId);

    /**
     * 根据id，获取已删除的机构主体
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.2
     */
    OrgUnit getOrgUnitDeletedById(String tenantId, String orgUnitId);

    /**
     * 根据id获得父对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    OrgUnit getParent(String tenantId, String orgUnitId);

    /**
     * 获得子节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return List&lt;OrgUnit&gt; 机构对象集合
     * @since 9.6.0
     */
    List<OrgUnit> getSubTree(String tenantId, String orgUnitId, String treeType);

    /**
     * 根据节点名称，和树类型查询机构节点
     *
     * @param tenantId 租户id
     * @param name 组织架构节点名称
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return List&lt;OrgUnit&gt; 机构对象集合
     * @since 9.6.0
     */
    List<OrgUnit> treeSearch(String tenantId, String name, String treeType);

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param tenantId 租户id
     * @param name 组织架构节点名称
     * @param dnName 路径名称
     * @param treeType 节点树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return List&lt;OrgUnit&gt; 机构对象集合
     * @since 9.6.0
     */
    List<OrgUnit> treeSearchByDn(String tenantId, String name, String treeType, String dnName);

}
