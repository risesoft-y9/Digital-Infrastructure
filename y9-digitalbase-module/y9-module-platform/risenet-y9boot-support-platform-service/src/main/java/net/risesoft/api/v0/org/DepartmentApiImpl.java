package net.risesoft.api.v0.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.org.DepartmentApi;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.DepartmentProp;
import net.risesoft.model.platform.org.Group;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.org.Position;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9EnumUtil;

/**
 * 部门服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0DepartmentApiImpl")
@RequestMapping(value = "/services/rest/department", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class DepartmentApiImpl implements DepartmentApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;
    private final Y9GroupService y9GroupService;
    private final Y9PersonService y9PersonService;
    private final Y9PositionService y9PositionService;
    private final Y9DepartmentPropService y9DepartmentPropService;

    /**
     * 新建部门
     *
     * @param tenantId 租户id
     * @param departmentJson 部门JSON字符串
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    public Department createDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentJson") @NotBlank String departmentJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Department department = Y9JsonUtil.readValue(departmentJson, Department.class);
        return y9DepartmentService.saveOrUpdate(department);
    }

    /**
     * 删除部门
     *
     * @param deptId 部门id
     * @param tenantId 租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    public boolean deleteDepartment(@RequestParam("deptId") @NotBlank String deptId,
        @RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.delete(deptId);
        return true;
    }

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return true：禁用成功，false：禁用失败
     * @since 9.6.0
     */
    @Override
    public boolean disableDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.changeDisable(departmentId);

        return true;
    }

    /**
     * 获取委办局
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public OrgUnit getBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnitBureau(departmentId).orElse(null);
    }

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    public Department getDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.findById(departmentId).orElse(null);
    }

    /**
     * 获取部门父节点
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public OrgUnit getParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnitParent(departmentId).orElse(null);
    }

    /**
     * 获取部门下的所有人员(递归，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code List<Person> }人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listAllPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.listAllDescendantPersons(departmentId);
    }

    /**
     * 获取部门下的所有未禁用/禁用的人员(递归)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return {@code List<Person> } 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listAllPersonsByDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.listAllDescendantPersons(departmentId, disabled);
    }

    /**
     * 根据是否禁用，人员姓名获取部门下所有人员
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @param name 人员姓名
     * @return {@code List<Person> } 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listAllPersonsByDisabledAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled,
        @RequestParam("name") @NotBlank String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.searchAllPersonsRecursionDownward(departmentId, name, disabled);
    }

    /**
     * 根据租户id和路径获取所有部门对象
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return {@code List<Department> } 部门对象集合
     * @since 9.6.0
     */
    @Override
    public List<Department> listByDn(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("dn") @NotBlank String dn) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.listByDn(dn, false);
    }

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @param category 配置类型 {@link DepartmentPropCategoryEnum}
     * @return {@code List<DepartmentProp> }
     * @since 9.6.0
     */
    @Override
    public List<DepartmentProp> listByOrgBaseIdAndCategory(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("category") Integer category) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentPropService.listByOrgBaseIdAndCategory(orgUnitId,
            Y9EnumUtil.valueOf(DepartmentPropCategoryEnum.class, category));
    }

    /**
     * 根据部门名称，模糊查询部门列表
     *
     * @param tenantId 租户id
     * @param deptName 部门名称
     * @return {@code List<Department> }
     * @since 9.6.0
     */
    @Override
    public List<Department> listByTenantIdAndDeptName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("deptName") @NotBlank String deptName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.listByNameLike(deptName, false);
    }

    /**
     * 获得一组部门对象
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return {@code List<Department> } 部门对象集合
     * @since 9.6.0
     */
    @Override
    public List<Department> listDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotEmpty List<String> ids) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.list(ids);
    }

    /**
     * 获取用户组(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code List<Group> } 用户组对象集合
     * @since 9.6.0
     */
    @Override
    public List<Group> listGroups(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9GroupService.listByParentId(departmentId, false);
    }

    /**
     * 获取部门领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code List<OrgUnit> } 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> listLeaders(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.listDepartmentPropOrgUnits(departmentId,
            DepartmentPropCategoryEnum.LEADER.getValue(), Boolean.FALSE, Boolean.FALSE);
    }

    /**
     * 获取部门主管领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code List<OrgUnit> } 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> listManagers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.listDepartmentPropOrgUnits(departmentId,
            DepartmentPropCategoryEnum.MANAGER.getValue(), Boolean.FALSE, Boolean.FALSE);
    }

    /**
     * 获取部门下的未删除的人员(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code List<Person> } 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonService.listByParentId(departmentId, false);
    }

    /**
     * 获取部门下的未禁用/禁用的人员(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return {@code List<Person> } 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersonsByDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PersonService.listByParentId(departmentId, disabled);
    }

    /**
     * 获取岗位(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code List<Position>} 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listPositions(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9PositionService.listByParentId(departmentId, false);
    }

    /**
     * 获取子部门(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code List<Department>} 部门对象集合
     * @since 9.6.0
     */
    @Override
    public List<Department> listSubDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.listByParentId(departmentId, false);
    }

    /**
     * 保存部门
     *
     * @param tenantId 租户id
     * @param departmentJson 部门json
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    public Department saveDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentJson") @NotBlank String departmentJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Department y9Department = Y9JsonUtil.readValue(departmentJson, Department.class);
        return y9DepartmentService.saveOrUpdate(y9Department);
    }
}
