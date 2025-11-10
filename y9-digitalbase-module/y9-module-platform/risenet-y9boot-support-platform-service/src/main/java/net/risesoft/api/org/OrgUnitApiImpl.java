package net.risesoft.api.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 实体服务组件
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
@RequestMapping(value = "/services/rest/v1/orgUnit", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrgUnitApiImpl implements OrgUnitApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrganizationService y9OrganizationService;

    /**
     * 获取组织节点所在委办局
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.findOrgUnitBureau(orgUnitId).orElse(null));
    }

    /**
     * 根据id获得组织节点对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getOrgUnit(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(compositeOrgBaseService.findOrgUnit(orgUnitId).orElse(null));
    }

    /**
     * 根据id获取已删除的组织节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象
     * @since 9.6.2
     */
    @Override
    public Y9Result<OrgUnit> getOrgUnitDeletedById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.findOrgUnitDeleted(orgUnitId).orElse(null));
    }

    /**
     * 根据id获得组织节点对象（人员或岗位）
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（人员或岗位）
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getOrgUnitPersonOrPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.findOrgUnitPersonOrPosition(orgUnitId).orElse(null));
    }

    /**
     * 获取组织节点所在的组织机构
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<Organization>} 通用请求返回对象 - data 是组织机构对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Organization> getOrganization(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.getOrgUnitOrganization(orgUnitId));
    }

    /**
     * 获取组织节点的父节点对象（部门或组织机构）<br>
     * 如果 orgUnitId 对应组织机构节点，则返回 null
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public Y9Result<OrgUnit> getParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.findOrgUnitParent(orgUnitId).orElse(null));
    }

    /**
     * 获得下一级组织节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @param treeType 树的类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> getSubTree(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("treeType") OrgTreeTypeEnum treeType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.getTree(orgUnitId, treeType, Boolean.FALSE));
    }

    /**
     * 获取组织树的根节点即组织机构列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Organization>> treeRoot(@RequestParam("tenantId") @NotBlank String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9OrganizationService.list(Boolean.FALSE, Boolean.FALSE));
    }

    /**
     * 根据节点名称和树类型查询组织节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 组织节点名称
     * @param treeType 树的类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> treeSearch(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name, @RequestParam("treeType") OrgTreeTypeEnum treeType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.treeSearch(name, treeType, Boolean.FALSE));
    }

    /**
     * 根据节点名称和结构树类型查询组织节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 组织节点名称
     * @param dnName 路径名称
     * @param treeType 树的类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> treeSearchByDn(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name, @RequestParam("treeType") OrgTreeTypeEnum treeType,
        @RequestParam("dnName") @NotBlank String dnName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(compositeOrgBaseService.treeSearch(name, treeType, dnName, Boolean.FALSE));
    }
}
