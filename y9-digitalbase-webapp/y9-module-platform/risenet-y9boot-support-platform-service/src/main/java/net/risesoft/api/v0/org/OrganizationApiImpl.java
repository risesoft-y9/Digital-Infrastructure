package net.risesoft.api.v0.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.org.OrganizationApi;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Group;
import net.risesoft.entity.Y9Organization;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.Y9Position;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Group;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
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
@Validated
@RestController(value = "v0OrganizationApiImpl")
@RequestMapping(value = "/services/rest/organization", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class OrganizationApiImpl implements OrganizationApi {

    private final Y9DepartmentService y9DepartmentService;
    private final Y9GroupService orgGroupService;
    private final Y9OrganizationService orgOrganizationService;
    private final Y9PersonService orgPersonService;
    private final Y9PositionService orgPositionService;

    /**
     * 根据id获得组织机构对象
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return Organization 对象
     * @since 9.6.0
     */
    @Override
    public Organization getOrganization(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Organization y9Organization = orgOrganizationService.findById(organizationId).orElse(null);
        return Y9ModelConvertUtil.convert(y9Organization, Organization.class);
    }

    /**
     * 获取所有委办局
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构id
     * @return {@code List<Department>} 部门对象集合
     * @since 9.6.0
     */
    @Override
    public List<Department> listAllBureaus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listBureau(organizationId, false);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 根据租户id获取所有组织机构
     *
     * @param tenantId 租户id
     * @return {@code List<Organization>} 组织机构对象集合
     * @since 9.6.0
     */
    @Override
    public List<Organization> listAllOrganizations(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Organization> y9OrganizationList = orgOrganizationService.list(false, false);
        return Y9ModelConvertUtil.convert(y9OrganizationList, Organization.class);
    }

    /**
     * 通过类型，获取组织机构列表
     *
     * @param tenantId 租户id
     * @param virtual 是否虚拟组织
     * @return {@code List<Organization>} 组织机构对象集合
     * @since 9.6.0
     */
    @Override
    public List<Organization> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("virtual") Boolean virtual) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Organization> y9OrganizationList = orgOrganizationService.list(virtual, false);
        return Y9ModelConvertUtil.convert(y9OrganizationList, Organization.class);
    }

    /**
     * 获取组织机构下的部门（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return {@code List<Department>} 部门对象集合
     * @since 9.6.0
     */
    @Override
    public List<Department> listDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(organizationId, false);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 获取用户组（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return {@code List<Group>} 用户组对象集合
     * @since 9.6.0
     */
    @Override
    public List<Group> listGroups(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Group> y9GroupList = orgGroupService.listByParentId(organizationId, false);
        return Y9ModelConvertUtil.convert(y9GroupList, Group.class);
    }

    /**
     * 获取人员（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return {@code List<Person>} 人员对象集合
     * @since 9.6.0
     */
    @Override
    public List<Person> listPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Person> y9PersonList = orgPersonService.listByParentId(organizationId, false);
        return Y9ModelConvertUtil.convert(y9PersonList, Person.class);
    }

    /**
     * 获取岗位（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return {@code List<Position>} 岗位对象集合
     * @since 9.6.0
     */
    @Override
    public List<Position> listPositions(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9Position> y9PositionList = orgPositionService.listByParentId(organizationId, false);
        return Y9ModelConvertUtil.convert(y9PositionList, Position.class);
    }

}
