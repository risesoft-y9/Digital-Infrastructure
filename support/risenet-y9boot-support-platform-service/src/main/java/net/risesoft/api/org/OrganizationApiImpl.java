package net.risesoft.api.org;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 机构服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@RestController
@RequestMapping(value = "/services/rest/organization", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrganizationApiImpl implements OrganizationApi {

    private final Y9DepartmentService y9DepartmentService;
    private final Y9GroupService orgGroupService;
    private final Y9OrganizationService orgOrganizationService;
    private final Y9PersonService orgPersonService;
    private final Y9PositionService orgPositionService;

    /**
     * 根据id获得机构对象
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return Organization 对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/get")
    public Organization getOrganization(@RequestParam String tenantId, @RequestParam String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9Organization y9Organization = orgOrganizationService.findById(organizationId);
        return Y9ModelConvertUtil.convert(y9Organization, Organization.class);
    }

    /**
     * 获取所有委办局
     *
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllBureaus")
    public List<Department> listAllBureaus(@RequestParam String tenantId, @RequestParam String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Department> y9DepartmentList = y9DepartmentService.listBureau(organizationId);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 根据租户id获取机构
     *
     * @param tenantId 租户id
     * @return List<Organization> 机构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping(value = "/listAllOrganizations")
    public List<Organization> listAllOrganizations(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Organization> y9OrganizationList = orgOrganizationService.list(false);
        return Y9ModelConvertUtil.convert(y9OrganizationList, Organization.class);
    }

    /**
     * 通过类型，获取组织架构列表
     *
     * @param tenantId 租户id
     * @param virtual 是否虚拟组织
     * @return List<Organization> 组织架构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByType")
    public List<Organization> listByType(@RequestParam String tenantId, @RequestParam Boolean virtual) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Organization> y9OrganizationList = orgOrganizationService.list(virtual);
        return Y9ModelConvertUtil.convert(y9OrganizationList, Organization.class);
    }

    /**
     * 获取机构下的部门（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listDepartments")
    public List<Department> listDepartments(@RequestParam String tenantId, @RequestParam String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(organizationId);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 获取用户组（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List<Group> 用户组对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listGroups")
    public List<Group> listGroups(@RequestParam String tenantId, @RequestParam String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Group> y9GroupList = orgGroupService.listByParentId(organizationId);
        return Y9ModelConvertUtil.convert(y9GroupList, Group.class);
    }

    /**
     * 获取人员（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersons")
    public List<Person> listPersons(@RequestParam String tenantId, @RequestParam String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Person> y9PersonList = orgPersonService.listByParentId(organizationId);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 获取岗位（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPositions")
    public List<Position> listPositions(@RequestParam String tenantId, @RequestParam String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Position> y9PositionList = orgPositionService.listByParentId(organizationId);
        return Y9ModelConvertUtil.convert(y9PositionList, Position.class);
    }

}
