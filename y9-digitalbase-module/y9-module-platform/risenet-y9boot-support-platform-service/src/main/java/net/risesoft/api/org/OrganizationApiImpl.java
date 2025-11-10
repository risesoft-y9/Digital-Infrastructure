package net.risesoft.api.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.dto.platform.CreateOrganizationDTO;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

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
@RestController
@RequestMapping(value = "/services/rest/v1/organization", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrganizationApiImpl implements OrganizationApi {

    private final Y9DepartmentService y9DepartmentService;
    private final Y9OrganizationService y9OrganizationService;

    /**
     * 根据id获得组织机构对象
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构唯一标识
     * @return {@code Y9Result<Organization>} 通用请求返回对象 - data 是组织机构对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Organization> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9OrganizationService.findById(organizationId).orElse(null));
    }

    @Override
    public Y9Result<Organization> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateOrganizationDTO createOrganizationDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Organization organization = PlatformModelConvertUtil.convert(createOrganizationDTO, Organization.class);
        return Y9Result.success(y9OrganizationService.saveOrUpdate(organization));
    }

    /**
     * 根据租户id获取组织机构列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Organization>> list(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9OrganizationService.list(Boolean.FALSE, Boolean.FALSE));
    }

    /**
     * 获取机构的委办局列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param organizationId 组织机构id
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listAllBureaus(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("organizationId") @NotBlank String organizationId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentService.listBureau(organizationId, Boolean.FALSE));
    }

    /**
     * 根据类型获取组织机构列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param virtual 是否虚拟组织
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Organization>> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("virtual") Boolean virtual) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9OrganizationService.list(virtual, Boolean.FALSE));
    }

}
