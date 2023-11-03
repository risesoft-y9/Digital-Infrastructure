package net.risesoft.api.org;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.dto.CreateDepartmentDTO;
import net.risesoft.enums.Y9DepartmentPropCategoryEnum;
import net.risesoft.model.Department;
import net.risesoft.model.DepartmentProp;
import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.pojo.Y9Result;

/**
 * 部门服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface DepartmentApi {

    /**
     * 新建部门
     *
     * @param tenantId 租户id
     * @param department 部门对象
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是保存的部门
     * @since 9.6.0
     */
    @PostMapping("/createDepartment")
    Y9Result<Department> createDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateDepartmentDTO department);

    /**
     * 删除部门
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @GetMapping("/deleteDepartment")
    Y9Result<Object> deleteDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("deptId") @NotBlank String deptId);

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @GetMapping("/disableDepartment")
    Y9Result<Object> disableDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 根据租户id和部门id，获取委办局
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @GetMapping("/getBureau")
    Y9Result<OrgUnit> getBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是部门对象
     * @since 9.6.0
     */
    @GetMapping("/getDepartment")
    Y9Result<Department> getDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门父节点
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @GetMapping("/getParent")
    Y9Result<OrgUnit> getParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门下的所有人员(递归，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllPersons")
    Y9Result<List<Person>> listAllPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门下的所有没有禁用/禁用的人员(递归,对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllPersonsByDisabled")
    Y9Result<List<Person>> listAllPersonsByDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled);

    /**
     * 根据是否禁用，人员姓名获取部门下所有人员
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @param name 人员姓名
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllPersonsByDisabledAndName")
    Y9Result<List<Person>> listAllPersonsByDisabledAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled,
        @RequestParam("name") @NotBlank String name);

    /**
     * 根据租户id和路径获取所有部门对象
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByDn")
    Y9Result<List<Department>> listByDn(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("dn") @NotBlank String dn);

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @param category 配置类型 {@link Y9DepartmentPropCategoryEnum}
     * @return {@code Y9Result<List<DepartmentProp>>} 通用请求返回对象 - data 是部门属性配置集合
     * @since 9.6.0
     */
    @GetMapping("/listByOrgBaseIdAndCategory")
    Y9Result<List<DepartmentProp>> listByOrgBaseIdAndCategory(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("category") Integer category);

    /**
     * 根据部门名称，模糊查询部门列表
     *
     * @param tenantId 租户id
     * @param deptName 部门名称
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门列表
     * @since 9.6.0
     */
    @GetMapping("/listByTenantIdAndDeptName")
    Y9Result<List<Department>> listByTenantIdAndDeptName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("deptName") @NotBlank String deptName);

    /**
     * 获得一组部门对象
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listDepartments")
    Y9Result<List<Department>> listDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotEmpty List<String> ids);

    /**
     * 获取用户组(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
     * @since 9.6.0
     */
    @GetMapping("/listGroups")
    Y9Result<List<Group>> listGroups(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listLeaders")
    Y9Result<List<OrgUnit>> listLeaders(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门主管领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listManagers")
    Y9Result<List<OrgUnit>> listManagers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门下的人员(下一级，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersons")
    Y9Result<List<Person>> listPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取部门下的没有禁用/禁用的人员(下一级，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersonsByDisabled")
    Y9Result<List<Person>> listPersonsByDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled);

    /**
     * 获取岗位(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPositions")
    Y9Result<List<Position>> listPositions(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 获取子部门(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSubDepartments")
    Y9Result<List<Department>> listSubDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 根据条件查询部门对象
     *
     * @param tenantId 租户id
     * @param whereClause sql语句where后面的条件语句
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/search")
    Y9Result<List<Department>> search(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("whereClause") @NotBlank String whereClause);

}