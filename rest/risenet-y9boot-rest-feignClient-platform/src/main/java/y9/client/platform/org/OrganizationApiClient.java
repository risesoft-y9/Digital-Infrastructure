package y9.client.platform.org;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.org.OrganizationApi;
import net.risesoft.model.Department;
import net.risesoft.model.Group;
import net.risesoft.model.Organization;
import net.risesoft.model.Person;
import net.risesoft.model.Position;

/**
 * 机构服务组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@FeignClient(contextId = "OrganizationApiClient", name = "y9platform", url = "${y9.common.orgBaseUrl}", path = "/services/rest/organization")
public interface OrganizationApiClient extends OrganizationApi {

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
    Organization getOrganization(@RequestParam("tenantId") String tenantId, @RequestParam("organizationId") String organizationId);

    /**
     * 返回所有委办局
     *
     * @param tenantId 租户id
     * @param organizationId 机构id
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllBureaus")
    List<Department> listAllBureaus(@RequestParam("tenantId") String tenantId, @RequestParam("organizationId") String organizationId);

    /**
     * 根据租户id获取所有机构
     *
     * @param tenantId 租户id
     * @return List&lt;Organization&gt; 机构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllOrganizations")
    List<Organization> listAllOrganizations(@RequestParam("tenantId") String tenantId);

    /**
     * 通过类型，获取组织架构列表
     *
     * @param tenantId 租户id
     * @param virtual 是否为虚拟组织
     * @return List&lt;Organization&gt; 组织架构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listByType")
    List<Organization> listByType(@RequestParam("tenantId") String tenantId, @RequestParam("virtual") Boolean virtual);

    /**
     * 获取机构下的部门（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Department&gt; 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listDepartments")
    List<Department> listDepartments(@RequestParam("tenantId") String tenantId, @RequestParam("organizationId") String organizationId);

    /**
     * 获取用户组（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Group&gt; 用户组对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listGroups")
    List<Group> listGroups(@RequestParam("tenantId") String tenantId, @RequestParam("organizationId") String organizationId);

    /**
     * 获取人员（下一级）
     *
     * @param tenantId 租户id
     * @param organizationId 机构唯一标识
     * @return List&lt;Person&gt; 人员对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listPersons")
    List<Person> listPersons(@RequestParam("tenantId") String tenantId, @RequestParam("organizationId") String organizationId);

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
    List<Position> listPositions(@RequestParam("tenantId") String tenantId, @RequestParam("organizationId") String organizationId);

}