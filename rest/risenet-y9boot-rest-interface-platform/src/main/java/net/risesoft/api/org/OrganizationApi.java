package net.risesoft.api.org;

import java.util.List;

import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.Position;

/**
 * 机构服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface OrganizationApi {

    /**
     * 根据id获得机构对象
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return Organization 对象
     * @since 9.6.0
     */
    Organization getOrganization(String tenantId, String organizationId);

    /**
     * 获取所有委办局
     *
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> listAllBureaus(String tenantId, String organizationId);

    /**
     * 根据租户id获取机构
     *
     * @param tenantId 租户id
     * @return List&lt;Organization&gt; 机构对象集合
     * @since 9.6.0
     */
    List<Organization> listAllOrganizations(String tenantId);

    /**
     * 通过类型，获取组织架构列表
     *
     * @param tenantId 租户id
     * @param virtual 是否虚拟组织
     * @return List&lt;Organization&gt; 组织架构对象集合
     * @since 9.6.0
     */
    List<Organization> listByType(String tenantId, Boolean virtual);

    /**
     * 获取机构下的部门（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> listDepartments(String tenantId, String organizationId);

    /**
     * 获取用户组（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    List<Group> listGroups(String tenantId, String organizationId);

    /**
     * 获取人员（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listPersons(String tenantId, String organizationId);

    /**
     * 获取岗位（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    List<Position> listPositions(String tenantId, String organizationId);
}