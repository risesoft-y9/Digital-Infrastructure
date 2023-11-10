package net.risesoft.api.org;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.pojo.Y9Result;

/**
 * 机构服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface OrganizationApi {

    /**
     * 根据id获得组织机构对象
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return Organization 对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Y9Result<Organization> getOrganization(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 获取所有委办局
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构id
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllBureaus")
    Y9Result<List<Department>> listAllBureaus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 根据租户id获取所有组织机构
     *
     * @param tenantId 租户id
     * @return List<Organization> 组织机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllOrganizations")
    Y9Result<List<Organization>> listAllOrganizations(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 通过类型，获取组织机构列表
     *
     * @param tenantId 租户id
     * @param virtual 是否虚拟组织
     * @return List<Organization> 组织机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByType")
    Y9Result<List<Organization>> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("virtual") Boolean virtual);

    /**
     * 获取组织机构下的部门（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listDepartments")
    Y9Result<List<Department>> listDepartments(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 获取用户组（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return List<Group> 用户组对象集合
     * @since 9.6.0
     */
    @GetMapping("/listGroups")
    Y9Result<List<Group>> listGroups(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 获取人员（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return List<Person> 人员对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPersons")
    Y9Result<List<Person>> listPersons(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 获取岗位（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return List&lt;Position&gt; 岗位对象集合
     * @since 9.6.0
     */
    @GetMapping("/listPositions")
    Y9Result<List<Position>> listPositions(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

}