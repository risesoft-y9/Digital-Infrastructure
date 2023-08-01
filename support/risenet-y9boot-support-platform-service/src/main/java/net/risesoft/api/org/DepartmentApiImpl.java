package net.risesoft.api.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.enums.Y9DepartmentPropCategoryEnum;
import net.risesoft.model.Department;
import net.risesoft.model.DepartmentProp;
import net.risesoft.model.Group;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
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
@RequestMapping(value = "/services/rest/department", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
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
     * @param departmentJson 部门JSON字符串
     * @return Department 部门对象
     * @since 9.6.0
     */
    @Override
    @PostMapping("/createDepartment")
    public Department createDepartment(@RequestParam String tenantId, @RequestParam String departmentJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Department y9Department = Y9JsonUtil.readValue(departmentJson, Y9Department.class);
        y9Department = y9DepartmentService.saveOrUpdate(y9Department, compositeOrgBaseService.getOrgBase(y9Department.getParentId()));
        return Y9ModelConvertUtil.convert(y9Department, Department.class);
    }

    /**
     * 删除部门
     *
     * @param departmentId 部门id
     * @param tenantId 租户id
     * @return boolean 是否删除成功
     * @since 9.6.0
     */
    @Override
    @GetMapping("/deleteDepartment")
    public boolean deleteDepartment(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.delete(departmentId);
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
    @GetMapping("/disableDepartment")
    public boolean disableDepartment(@RequestParam String tenantId, @RequestParam String departmentId) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(departmentId)) {
            return false;
        }

        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.changeDisable(departmentId);

        return true;
    }

    /**
     * 获取委办局
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return OrgUnit 组织机构节点对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getBureau")
    public OrgUnit getBureau(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9OrgBase bureau = compositeOrgBaseService.getOrgUnitBureau(departmentId);
        return ModelConvertUtil.orgBaseToOrgUnit(bureau);
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
    @GetMapping("/getDepartment")
    public Department getDepartment(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Department y9Department = y9DepartmentService.findById(departmentId);
        return Y9ModelConvertUtil.convert(y9Department, Department.class);
    }

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
    public OrgUnit getParent(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9OrgBase parent = compositeOrgBaseService.getParent(departmentId);
        return ModelConvertUtil.orgBaseToOrgUnit(parent);
    }

    /**
     * 获取部门下的所有人员(递归，包含部门下对应的人员)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersons")
    public List<Person> listAllPersons(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = compositeOrgBaseService.listAllPersonsRecursionDownward(departmentId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 获取部门下的所有未禁用/禁用的人员(递归)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersonsByDisabled")
    public List<Person> listAllPersonsByDisabled(@RequestParam String tenantId, @RequestParam String departmentId, @RequestParam Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = compositeOrgBaseService.listAllPersonsRecursionDownward(departmentId, disabled);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 根据是否禁用，人员姓名获取部门下所有人员
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @param name 人员姓名
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllPersonsByDisabledAndName")
    public List<Person> listAllPersonsByDisabledAndName(@RequestParam String tenantId,
        @RequestParam String departmentId, @RequestParam Boolean disabled, @RequestParam String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = compositeOrgBaseService.searchAllPersonsRecursionDownward(departmentId, disabled, name);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 根据租户id和路径获取所有部门对象
     *
     * @param tenantId 租户id
     * @param dn 路径
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByDn")
    public List<Department> listByDn(@RequestParam String tenantId, @RequestParam String dn) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByDn(dn);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgBaseId 组织节点id
     * @param category 配置类型 {@link Y9DepartmentPropCategoryEnum}
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByOrgBaseIdAndCategory")
    public List<DepartmentProp> listByOrgBaseIdAndCategory(@RequestParam String tenantId, @RequestParam String orgBaseId, @RequestParam Integer category) {
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(orgBaseId)) {
            return new ArrayList<>();
        }
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9DepartmentProp> y9DepartmentPropList = y9DepartmentPropService.listByOrgBaseIdAndCategory(orgBaseId, category);
        return Y9ModelConvertUtil.convert(y9DepartmentPropList, DepartmentProp.class);
    }

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
    public List<Department> listByTenantIdAndDeptName(@RequestParam String tenantId, @RequestParam String deptName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByNameLike(deptName);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

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
    public List<Department> listDepartments(@RequestParam String tenantId, @RequestParam List<String> ids) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.list(ids);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 获取用户组(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List<Group> 用户组对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listGroups")
    public List<Group> listGroups(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Group> y9GroupList = y9GroupService.listByParentId(departmentId);
        return Y9ModelConvertUtil.convert(y9GroupList, Group.class);
    }

    /**
     * 获取部门领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List<OrgUnit> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listLeaders")
    public List<OrgUnit> listLeaders(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> y9OrgBaseList = y9DepartmentService.listLeaders(departmentId);
        return ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList);
    }

    /**
     * 获取部门主管领导
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List<OrgUnit> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listManagers")
    public List<OrgUnit> listManagers(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> y9OrgBaseList = y9DepartmentService.listManagers(departmentId);
        return ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList);
    }

    /**
     * 获取部门下的未删除的人员(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List<Person> 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersons")
    public List<Person> listPersons(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByParentId(departmentId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 获取部门下的未禁用/禁用的人员(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @param disabled 是否禁用
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersonsByDisabled")
    public List<Person> listPersonsByDisabled(@RequestParam String tenantId, @RequestParam String departmentId, @RequestParam Boolean disabled) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = y9PersonService.listByParentIdAndDisabled(departmentId, disabled);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 获取岗位(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List<Position> 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPositions")
    public List<Position> listPositions(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = y9PositionService.listByParentId(departmentId);
        return Y9ModelConvertUtil.convert(y9PositionList, Position.class);
    }

    /**
     * 获取子部门(下一级)
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listSubDepartments")
    public List<Department> listSubDepartments(@RequestParam String tenantId, @RequestParam String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(departmentId);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
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
    @PostMapping("/saveDepartment")
    public Department saveDepartment(@RequestParam String tenantId, @RequestParam String departmentJson) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Department y9Department = Y9JsonUtil.readValue(departmentJson, Y9Department.class);
        y9Department = y9DepartmentService.saveOrUpdate(y9Department, compositeOrgBaseService.getOrgBase(y9Department.getParentId()));
        return Y9ModelConvertUtil.convert(y9Department, Department.class);
    }

    /**
     * 根据条件查询部门对象
     *
     * @param tenantId 租户id
     * @param whereClause sql语句where后面的条件语句
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/search")
    public List<Department> search(@RequestParam String tenantId, @RequestParam String whereClause) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.search(whereClause);
        Collections.sort(y9DepartmentList);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }
}
