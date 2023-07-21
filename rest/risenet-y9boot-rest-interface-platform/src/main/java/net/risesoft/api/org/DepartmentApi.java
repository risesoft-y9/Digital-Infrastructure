package net.risesoft.api.org;

import java.util.List;

import net.risesoft.enums.Y9DepartmentPropCategoryEnum;
import net.risesoft.model.Department;
import net.risesoft.model.DepartmentProp;
import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;

/**
 * 部门服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface DepartmentApi {

    /**
     * 新建部门
     *
     * @param tenantId 租户id
     * @param departmentJson 部门JSON字符串
     * @return Department 部门对象
     * @since 9.6.0
     */
    Department createDepartment(String tenantId, String departmentJson);

    /**
     * 删除部门
     *
     * @param deptId 部门id
     * @param tenantId 租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    boolean deleteDepartment(String deptId, String tenantId);

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return true：禁用成功，false：禁用失败
     * @since 9.6.0
     */
    boolean disableDepartment(String tenantId, String departmentId);

    /**
     * 获取委办局
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return OrgUnit 组织机构节点对象
     * @since 9.6.0
     */
    OrgUnit getBureau(String tenantId, String departmentId);

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return Department 部门对象
     * @since 9.6.0
     */
    Department getDepartment(String tenantId, String departmentId);

    /**
     * 获取部门父节点
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return OrgUnit 组织机构节点对象
     * @since 9.6.0
     */
    OrgUnit getParent(String tenantId, String departmentId);

    /**
     * 获取部门下的所有人员(递归，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listAllPersons(String tenantId, String departmentId);

    /**
     * 获取部门下的所有未禁用/禁用的人员(递归)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listAllPersonsByDisabled(String tenantId, String departmentId, Boolean disabled);

    /**
     * 根据是否禁用，人员姓名获取部门下所有人员
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @param name 人员姓名
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listAllPersonsByDisabledAndName(String tenantId, String departmentId, Boolean disabled, String name);

    /**
     * 根据租户id和路径获取所有部门对象
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> listByDn(String tenantId, String dn);

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgBaseId 组织节点id
     * @param category 配置类型 {@link Y9DepartmentPropCategoryEnum}
     * @return List&lt;DepartmentProp&gt; 部门部门属性配置集合
     * @since 9.6.0
     */
    List<DepartmentProp> listByOrgBaseIdAndCategory(String tenantId, String orgBaseId, Integer category);

    /**
     * 根据部门名称，模糊查询部门列表
     *
     * @param tenantId 租户id
     * @param deptName 部门名称
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> listByTenantIdAndDeptName(String tenantId, String deptName);

    /**
     * 获得一组部门对象
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> listDepartments(String tenantId, List<String> ids);

    /**
     * 获取用户组(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    List<Group> listGroups(String tenantId, String departmentId);

    /**
     * 获取部门领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;OrgUnit&gt; 人员对象集合
     * @since 9.6.0
     */
    List<OrgUnit> listLeaders(String tenantId, String departmentId);

    /**
     * 获取部门主管领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;OrgUnit&gt; 人员对象集合
     * @since 9.6.0
     */
    List<OrgUnit> listManagers(String tenantId, String departmentId);

    /**
     * 获取部门下的未删除的人员(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Person&gt; 岗位对象集合
     * @since 9.6.0
     */
    List<Person> listPersons(String tenantId, String departmentId);

    /**
     * 获取部门下的未禁用/禁用的人员(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    List<Person> listPersonsByDisabled(String tenantId, String departmentId, Boolean disabled);

    /**
     * 获取岗位(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    List<Position> listPositions(String tenantId, String departmentId);

    /**
     * 获取子部门(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> listSubDepartments(String tenantId, String departmentId);

    /**
     * 保存部门
     *
     * @param tenantId 租户id
     * @param departmentJson 部门对象
     * @return Department 部门对象
     * @since 9.6.0
     */
    Department saveDepartment(String tenantId, String departmentJson);

    /**
     * 根据条件查询部门对象
     *
     * @param tenantId 租户id
     * @param whereClause sql语句where后面的条件语句
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    List<Department> search(String tenantId, String whereClause);
}