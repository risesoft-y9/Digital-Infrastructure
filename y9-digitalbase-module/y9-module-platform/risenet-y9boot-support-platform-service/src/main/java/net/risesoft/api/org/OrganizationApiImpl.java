package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrganizationApi;
import net.risesoft.api.platform.org.dto.CreateOrganizationDTO;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9Organization;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Organization;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9OrganizationService;
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
@RestController
@RequestMapping(value = "/services/rest/v1/organization", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrganizationApiImpl implements OrganizationApi {

    private final Y9DepartmentService y9DepartmentService;
    private final Y9OrganizationService orgOrganizationService;

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

        Y9Organization y9Organization = orgOrganizationService.findById(organizationId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Organization, Organization.class));
    }

    @Override
    public Y9Result<Organization> create(String tenantId, CreateOrganizationDTO organization) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Organization y9Organization = Y9ModelConvertUtil.convert(organization, Y9Organization.class);
        y9Organization = orgOrganizationService.saveOrUpdate(y9Organization);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Organization, Organization.class));
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

        List<Y9Organization> y9OrganizationList = orgOrganizationService.list(Boolean.FALSE, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9OrganizationList, Organization.class));
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

        List<Y9Department> y9DepartmentList = y9DepartmentService.listBureau(organizationId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
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

        List<Y9Organization> y9OrganizationList = orgOrganizationService.list(virtual, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9OrganizationList, Organization.class));
    }

}
