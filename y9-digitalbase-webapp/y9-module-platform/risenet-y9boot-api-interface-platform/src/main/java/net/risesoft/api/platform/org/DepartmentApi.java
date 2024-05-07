package net.risesoft.api.platform.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.platform.org.dto.CreateDepartmentDTO;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.DepartmentProp;
import net.risesoft.model.platform.OrgUnit;
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
    @PostMapping("/create")
    Y9Result<Department> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateDepartmentDTO department);

    /**
     * 删除部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @PostMapping("/disable")
    Y9Result<Object> disable(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是部门对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Y9Result<Department> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId);

    /**
     * 根据id列表获得部门对象列表
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByIds")
    Y9Result<List<Department>> listByIds(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotEmpty List<String> ids);

    /**
     * 根据部门名称模糊查询部门列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 部门名称
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门列表
     * @since 9.6.0
     */
    @GetMapping("/listByName")
    Y9Result<List<Department>> listByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name);

    /**
     * 获取下一级部门列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门唯一标识
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByParentId")
    Y9Result<List<Department>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId);

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @param category 配置类型 {@link DepartmentPropCategoryEnum}
     * @return {@code Y9Result<List<DepartmentProp>>} 通用请求返回对象 - data 是部门属性配置集合
     * @since 9.6.0
     */
    @GetMapping("/listDepartmentPropByOrgUnitIdAndCategory")
    Y9Result<List<DepartmentProp>> listDepartmentPropByOrgUnitIdAndCategory(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("orgUnitId") @NotBlank String orgUnitId,
        @RequestParam("category") DepartmentPropCategoryEnum category);

    /**
     * 获取部门属性对应组织节点列表
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @param category 部门属性类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员或岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listDepartmentPropOrgUnits")
    Y9Result<List<OrgUnit>> listDepartmentPropOrgUnits(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam Integer category);

    /**
     * 递归获得所有层级子部门列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 组织节点唯一标识(可能是机构ID,也可能是部门ID)
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listRecursivelyByParentId")
    Y9Result<List<Department>> listRecursivelyByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId);

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