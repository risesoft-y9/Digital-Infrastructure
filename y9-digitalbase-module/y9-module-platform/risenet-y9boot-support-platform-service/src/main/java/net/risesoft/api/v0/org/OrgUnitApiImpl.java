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

import net.risesoft.api.platform.v0.org.OrgUnitApi;
import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9EnumUtil;

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
@RestController(value = "v0OrgUnitApiImpl")
@RequestMapping(value = "/services/rest/orgUnit", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class OrgUnitApiImpl implements OrgUnitApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;

    /**
     * 根据租户id和节点id获取委办局
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public OrgUnit getBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnitBureau(orgUnitId).orElse(null);
    }

    /**
     * 获得部门树
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识(可能是机构ID,也可能是部门ID)
     * @return {@code List<Department>} 部门对象集合
     * @since 9.6.0
     */
    @Override
    public List<Department> getDeptTrees(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return y9DepartmentService.listRecursivelyByParentId(orgUnitId, Boolean.FALSE);
    }

    /**
     * 根据id获得组织节点对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return OrgUnit 组织节点对象
     * @since 9.6.0
     */
    @Override
    public OrgUnit getOrgUnit(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnit(orgUnitId).orElse(null);
    }

    /**
     * 根据id，获取已删除的组织节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return OrgUnit 组织节点对象
     * @since 9.6.2
     */
    @Override
    public OrgUnit getOrgUnitDeletedById(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnitDeleted(orgUnitId).orElse(null);
    }

    /**
     * 获取组织节点所在的组织机构
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return Organization 组织机构对象
     * @since 9.6.0
     */
    @Override
    public Organization getOrganization(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.getOrgUnitOrganization(orgUnitId);
    }

    /**
     * 根据id获得父对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return OrgUnit 组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @Override
    public OrgUnit getParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.findOrgUnitParent(orgUnitId).orElse(null);
    }

    /**
     * 获得子节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return {@code List<OrgUnit>} 组织节点对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> getSubTree(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("treeType") @NotBlank String treeType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.getTree(orgUnitId, Y9EnumUtil.valueOf(OrgTreeTypeEnum.class, treeType), false);
    }

    /**
     * 根据节点名称，和树类型查询组织节点
     *
     * @param tenantId 租户id
     * @param name 组织节点名称
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门），tree_type_group（用户组），tree_type_position（岗位）
     *            tree_type_person（人员），tree_type_bureau（委办局）
     * @return {@code List<OrgUnit>} 组织节点对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> treeSearch(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name, @RequestParam("treeType") @NotBlank String treeType) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.treeSearch(name, Y9EnumUtil.valueOf(OrgTreeTypeEnum.class, treeType), false);
    }

    /**
     * 根据name，和结构树类型查询组织节点
     *
     * @param tenantId 租户id
     * @param name 组织节点名称
     * @param dnName 路径名称
     * @param treeType 节点树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return {@code List<OrgUnit>} 组织节点对象集合
     * @since 9.6.0
     */
    @Override
    public List<OrgUnit> treeSearchByDn(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name, @RequestParam("treeType") @NotBlank String treeType,
        @RequestParam("dnName") @NotBlank String dnName) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return compositeOrgBaseService.treeSearch(name, Y9EnumUtil.valueOf(OrgTreeTypeEnum.class, treeType), dnName,
            false);
    }

}
