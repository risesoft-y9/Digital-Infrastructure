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
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.model.Department;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Organization;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

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
@RestController
@RequestMapping(value = "/services/rest/orgUnit", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrgUnitApiImpl implements OrgUnitApi {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;

    /**
     * 根据租户id和节点id获取委办局
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getBureau")
    public OrgUnit getBureau(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return ModelConvertUtil.orgBaseToOrgUnit(compositeOrgBaseService.getOrgUnitBureau(orgUnitId));
    }

    /**
     * 获得部门树
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识(可能是机构ID,也可能是部门ID)
     * @return List<Department> 部门对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getDeptTrees")
    public List<Department> getDeptTrees(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9Department> y9DepartmentList = y9DepartmentService.getDeptTrees(orgUnitId);
        return Y9ModelConvertUtil.convert(y9DepartmentList, Department.class);
    }

    /**
     * 获取组织节点所在的组织机构
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return Organization 机构对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getOrganization")
    public Organization getOrganization(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9OrgBase y9OrgBase = compositeOrgBaseService.getOrgUnitOrganization(orgUnitId);
        return Y9ModelConvertUtil.convert(y9OrgBase, Organization.class);
    }

    /**
     * 根据id获得机构对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/get")
    public OrgUnit getOrgUnit(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return ModelConvertUtil.orgBaseToOrgUnit(compositeOrgBaseService.getOrgBase(orgUnitId));
    }

    /**
     * 根据id，获取已删除的机构主体
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.2
     */
    @Override
    @GetMapping("/getOrgUnitDeletedById")
    public OrgUnit getOrgUnitDeletedById(String tenantId, String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        return ModelConvertUtil.orgBaseToOrgUnit(compositeOrgBaseService.getOrgBaseDeletedByOrgUnitId(orgUnitId));
    }

    /**
     * 根据id获得父对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构唯一标识
     * @return OrgUnit 机构对象
     * @since 9.6.0
     */
    @Override
    @GetMapping("/getParent")
    public OrgUnit getParent(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        Y9OrgBase parent = compositeOrgBaseService.getParent(orgUnitId);
        return ModelConvertUtil.orgBaseToOrgUnit(parent);
    }

    /**
     * 获得子节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 机构节点唯一标识
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return List<OrgUnit> 机构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping(value = "/getSubTree")
    public List<OrgUnit> getSubTree(@RequestParam String tenantId, @RequestParam String orgUnitId, @RequestParam String treeType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9OrgBase> y9OrgBaseList = compositeOrgBaseService.getTree(orgUnitId, treeType, false);
        return ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList);
    }

    /**
     * 根据节点名称，和树类型查询机构节点
     *
     * @param tenantId 租户id
     * @param name 组织架构节点名称
     * @param treeType 树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return List<OrgUnit> 机构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/treeSearch")
    public List<OrgUnit> treeSearch(@RequestParam String tenantId, @RequestParam String name, @RequestParam String treeType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9OrgBase> y9OrgBaseList = compositeOrgBaseService.treeSearch(name, treeType, false);
        return ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList);
    }

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param tenantId 租户id
     * @param name 组织架构节点名称
     * @param dnName 路径名称
     * @param treeType 节点树的类型:tree_type_org(组织机构)，tree_type_dept（部门） tree_type_group（用户组）, tree_type_position（岗位）
     *            tree_type_person（人员）, tree_type_bureau（委办局）
     * @return List<OrgUnit> 机构对象集合
     * @since 9.6.0
     */
    @Override
    @GetMapping("/treeSearchByDn")
    public List<OrgUnit> treeSearchByDn(@RequestParam String tenantId, @RequestParam String name, @RequestParam String treeType, @RequestParam String dnName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        
        List<Y9OrgBase> y9OrgBaseList = compositeOrgBaseService.treeSearch(name, treeType, dnName);
        return ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList);
    }

}
