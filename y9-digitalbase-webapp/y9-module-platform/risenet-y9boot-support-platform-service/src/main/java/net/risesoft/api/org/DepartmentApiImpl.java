package net.risesoft.api.org;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

import net.risesoft.api.platform.org.DepartmentApi;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.dto.CreateDepartmentDTO;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.DepartmentProp;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

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
@RestController
@RequestMapping(value = "/services/rest/v1/department", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
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
     * @param department 部门对象
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是保存的部门
     * @since 9.6.0
     */
    @Override
    public Y9Result<Department> createDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateDepartmentDTO department) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Department y9Department = Y9ModelConvertUtil.convert(department, Y9Department.class);
        y9Department = y9DepartmentService.saveOrUpdate(y9Department);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Department, Department.class));
    }

    /**
     * 删除部门
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> deleteDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("deptId") @NotBlank String deptId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.delete(deptId);
        return Y9Result.success();
    }

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> disableDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.changeDisable(departmentId);

        return Y9Result.success();
    }

    /**
     * 根据租户id和部门id，获取委办局
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9OrgBase bureau = compositeOrgBaseService.findOrgUnitBureau(departmentId).orElse(null);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(bureau));
    }

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是部门对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Department> getDepartment(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Department y9Department = y9DepartmentService.findById(departmentId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Department, Department.class));
    }

    /**
     * 获取部门父节点
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9OrgBase parent = compositeOrgBaseService.findOrgUnitParent(departmentId).orElse(null);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(parent));
    }

    /**
     * 获取部门下的所有人员(递归，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listAllPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = compositeOrgBaseService.listAllPersonsRecursionDownward(departmentId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取部门下的所有没有禁用/禁用的人员(递归,对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listAllPersonsByDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = compositeOrgBaseService.listAllPersonsRecursionDownward(departmentId, disabled);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

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
    @Override
    public Y9Result<List<Person>> listAllPersonsByDisabledAndName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled,
        @RequestParam("name") @NotBlank String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList =
            compositeOrgBaseService.searchAllPersonsRecursionDownward(departmentId, disabled, name);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 根据租户id和路径获取所有部门对象
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listByDn(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("dn") @NotBlank String dn) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByDn(dn);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
    }

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @param category 配置类型 {@link DepartmentPropCategoryEnum}
     * @return {@code Y9Result<List<DepartmentProp>>} 通用请求返回对象 - data 是部门属性配置集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<DepartmentProp>> listByOrgBaseIdAndCategory(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("orgUnitId") @NotBlank String orgUnitId,
        @RequestParam("category") DepartmentPropCategoryEnum category) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9DepartmentProp> y9DepartmentPropList =
            y9DepartmentPropService.listByOrgBaseIdAndCategory(orgUnitId, category);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentPropList, DepartmentProp.class));
    }

    /**
     * 根据部门名称，模糊查询部门列表
     *
     * @param tenantId 租户id
     * @param deptName 部门名称
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listByTenantIdAndDeptName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("deptName") @NotBlank String deptName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByNameLike(deptName);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
    }

    /**
     * 获得一组部门对象
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotEmpty List<String> ids) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.list(ids);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
    }

    /**
     * 获取用户组(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Group>>} 通用请求返回对象 - data 是用户组对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Group>> listGroups(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Group> y9GroupList = y9GroupService.listByParentId(departmentId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9GroupList, Group.class));
    }

    /**
     * 获取部门领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> listLeaders(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> y9OrgBaseList = y9DepartmentService.listLeaders(departmentId);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList));
    }

    /**
     * 获取部门主管领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> listManagers(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> y9OrgBaseList = y9DepartmentService.listManagers(departmentId);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList));
    }

    /**
     * 获取部门下的人员(下一级，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByParentId(departmentId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取部门下的没有禁用/禁用的人员(下一级，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return {@code Y9Result<List<Person>>} 通用请求返回对象 - data 是人员对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Person>> listPersonsByDisabled(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("disabled") Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByParentIdAndDisabled(departmentId, disabled);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PersonList, Person.class));
    }

    /**
     * 获取岗位(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Position>>} 通用请求返回对象 - data 是岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Position>> listPositions(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByParentId(departmentId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9PositionList, Position.class));
    }

    /**
     * 获取子部门(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listSubDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(departmentId);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
    }

    /**
     * 根据条件查询部门对象
     *
     * @param tenantId 租户id
     * @param whereClause sql语句where后面的条件语句
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> search(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("whereClause") @NotBlank String whereClause) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.search(whereClause);
        Collections.sort(y9DepartmentList);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
    }
}
