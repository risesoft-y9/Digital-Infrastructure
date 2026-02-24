package net.risesoft.api.platform.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.dto.platform.CreateOrganizationDTO;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Organization;
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
     * @return {@code Y9Result<Organization>} 通用请求返回对象 - data 是组织机构对象
     * @since 9.6.0
     */
    @GetMapping("/get")
    Y9Result<Organization> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 新建组织
     *
     * @param tenantId 租户id
     * @param organization 组织对象
     * @return {@code Y9Result<Organization>} 通用请求返回对象 - data 是保存的组织
     * @since 9.6.0
     */
    @PostMapping("/create")
    Y9Result<Organization> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateOrganizationDTO organization);

    /**
     * 根据租户id获取组织机构列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/list")
    Y9Result<List<Organization>> list(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据id列表批量获取组织机构对象
     *
     * @param tenantId 租户id
     * @param ids 组织机构唯一标识
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象列表
     * @since 9.6.10
     */
    @GetMapping("/listByIds")
    Y9Result<List<Organization>> listByIds(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotBlank List<String> ids);

    /**
     * 获取机构的委办局列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构id
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @GetMapping("/listAllBureaus")
    Y9Result<List<Department>> listAllBureaus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId);

    /**
     * 根据类型获取组织机构列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param virtual 是否虚拟组织
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/listByType")
    Y9Result<List<Organization>> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("virtual") Boolean virtual);

}