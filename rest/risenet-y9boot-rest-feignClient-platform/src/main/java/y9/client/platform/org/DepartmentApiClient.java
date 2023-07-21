package y9.client.platform.org;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.DepartmentApi;
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
@FeignClient(contextId = "DepartmentApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/department")
public interface DepartmentApiClient extends DepartmentApi {

    /**
     * 新建部门
     *
     * @param tenantId 租户id
     * @param departmentJson 部门对象
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/createDepartment")
    Department createDepartment(@RequestParam("tenantId") String tenantId, @RequestParam("departmentJson") String departmentJson);

    /**
     * 删除部门
     *
     * @param deptId 部门id
     * @param tenantId 租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    @GetMapping("/deleteDepartment")
    boolean deleteDepartment(@RequestParam("deptId") String deptId, @RequestParam("tenantId") String tenantId);

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return true：禁用成功，false：禁用失败
     * @since 9.6.0
     */
    @Override
    @GetMapping("/disableDepartment")
    boolean disableDepartment(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 根据租户id和部门id，获取委办局
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return OrgUnit 组织机构节点对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getBureau")
    OrgUnit getBureau(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getDepartment")
    Department getDepartment(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门父节点
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return OrgUnit 组织机构节点对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getParent")
    OrgUnit getParent(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门下的所有人员(递归，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersons")
    List<Person> listAllPersons(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门下的所有没有禁用/禁用的人员(递归,对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersonsByDisabled")
    List<Person> listAllPersonsByDisabled(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId, @RequestParam("disabled") Boolean disabled);

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
    @Override
    @GetMapping("/listAllPersonsByDisabledAndName")
    List<Person> listAllPersonsByDisabledAndName(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId, @RequestParam("disabled") Boolean disabled, @RequestParam("name") String name);

    /**
     * 根据租户id和路径获取所有部门对象
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByDn")
    List<Department> listByDn(@RequestParam("tenantId") String tenantId, @RequestParam("dn") String dn);

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgBaseId 组织节点id
     * @param category 配置类型 {@link Y9DepartmentPropCategoryEnum}
     * @return List&lt;DepartmentProp&gt; 部门部门属性配置集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByOrgBaseIdAndCategory")
    List<DepartmentProp> listByOrgBaseIdAndCategory(@RequestParam("tenantId") String tenantId, @RequestParam("orgBaseId") String orgBaseId, @RequestParam("category") Integer category);

    /**
     * 根据部门名称，模糊查询部门列表
     *
     * @param tenantId 租户id
     * @param deptName 部门名称
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByTenantIdAndDeptName")
    List<Department> listByTenantIdAndDeptName(@RequestParam("tenantId") String tenantId, @RequestParam("deptName") String deptName);

    /**
     * 获得一组部门对象
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listDepartments")
    List<Department> listDepartments(@RequestParam("tenantId") String tenantId, @RequestParam("ids") List<String> ids);

    /**
     * 获取用户组(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listGroups")
    List<Group> listGroups(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listLeaders")
    List<OrgUnit> listLeaders(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门主管领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listManagers")
    List<OrgUnit> listManagers(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门下的人员(下一级，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Person&gt; 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersons")
    List<Person> listPersons(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取部门下的没有禁用/禁用的人员(下一级，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersonsByDisabled")
    List<Person> listPersonsByDisabled(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId, @RequestParam("disabled") Boolean disabled);

    /**
     * 获取岗位(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPositions")
    List<Position> listPositions(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 获取子部门(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubDepartments")
    List<Department> listSubDepartments(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String departmentId);

    /**
     * 保存部门
     *
     * @param tenantId 租户id
     * @param departmentJson 部门对象
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/saveDepartment")
    Department saveDepartment(@RequestParam("tenantId") String tenantId, @RequestParam("departmentJson") String departmentJson);

    /**
     * 根据条件查询部门对象
     *
     * @param tenantId 租户id
     * @param whereClause sql语句where后面的条件语句
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/search")
    List<Department> search(@RequestParam("tenantId") String tenantId, @RequestParam("whereClause") String whereClause);
}