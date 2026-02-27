package net.risesoft.api.platform.org;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.org.OrgTreeTypeEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Organization;
import net.risesoft.pojo.Y9Result;

/**
 * 组织节点组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface OrgUnitApi {

    /**
     * 获取组织节点所在委办局
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @GetMapping("/getOrgUnitBureau")
    Y9Result<OrgUnit> getOrgUnitBureau(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据id获得组织节点对象
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象
     * @since 9.6.0
     */
    @GetMapping("/getOrgUnit")
    Y9Result<OrgUnit> getOrgUnit(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据id获取已删除的组织节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象
     * @since 9.6.2
     */
    @GetMapping("/getOrgUnitDeleted")
    Y9Result<OrgUnit> getOrgUnitDeleted(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 根据id获得组织节点对象（人员或岗位）
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（人员或岗位）
     * @since 9.6.0
     */
    @GetMapping("/getPersonOrPosition")
    Y9Result<OrgUnit> getPersonOrPosition(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 获取组织节点所在的组织机构
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<Organization>} 通用请求返回对象 - data 是组织机构对象
     * @since 9.6.0
     */
    @GetMapping("/getOrgUnitOrganization")
    Y9Result<Organization> getOrgUnitOrganization(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 获取组织节点的父节点对象（部门或组织机构）<br>
     * 如果 orgUnitId 对应组织机构节点，则返回 null
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.0
     */
    @GetMapping("/getOrgUnitParent")
    Y9Result<OrgUnit> getOrgUnitParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 获取作为父节点的组织节点对象（部门或组织机构）<br>
     * 如果 orgUnitId 对应组织机构节点，则返回 null
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @return {@code Y9Result<OrgUnit>} 通用请求返回对象 - data 是组织节点对象（部门或组织机构）
     * @since 9.6.10
     */
    @GetMapping("/getOrgUnitAsParent")
    Y9Result<OrgUnit> getOrgUnitAsParent(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId);

    /**
     * 获得下一级组织节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点唯一标识
     * @param treeType 树的类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点对象集合
     * @since 9.6.0
     */
    @GetMapping("/getSubTree")
    Y9Result<List<OrgUnit>> getSubTree(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("treeType") OrgTreeTypeEnum treeType);

    /**
     * 获取组织树的根节点即组织机构列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Organization>>} 通用请求返回对象 - data 是组织机构对象集合
     * @since 9.6.0
     */
    @GetMapping("/treeRoot")
    Y9Result<List<Organization>> treeRoot(@RequestParam("tenantId") @NotBlank String tenantId);

    /**
     * 根据节点名称和树类型查询组织节点列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 组织节点名称
     * @param treeType 树的类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是组织节点对象集合
     * @since 9.6.0
     */
    @GetMapping("/treeSearch")
    Y9Result<List<OrgUnit>> treeSearch(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name, @RequestParam("treeType") OrgTreeTypeEnum treeType);

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
    @GetMapping("/treeSearchByDn")
    Y9Result<List<OrgUnit>> treeSearchByDn(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name, @RequestParam("treeType") OrgTreeTypeEnum treeType,
        @RequestParam("dnName") @NotBlank String dnName);

}
