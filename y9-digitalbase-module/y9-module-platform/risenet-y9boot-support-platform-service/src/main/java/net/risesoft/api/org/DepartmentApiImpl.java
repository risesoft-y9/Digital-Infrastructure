package net.risesoft.api.org;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.dto.platform.CreateDepartmentDTO;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.DepartmentProp;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 部门服务组件
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
@RequestMapping(value = "/services/rest/v1/department", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class DepartmentApiImpl implements DepartmentApi {

    private final Y9DepartmentService y9DepartmentService;
    private final Y9DepartmentPropService y9DepartmentPropService;

    /**
     * 新建部门
     *
     * @param tenantId 租户id
     * @param createDepartmentDTO 部门对象
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是保存的部门
     * @since 9.6.0
     */
    @Override
    public Y9Result<Department> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateDepartmentDTO createDepartmentDTO) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Department department = PlatformModelConvertUtil.convert(createDepartmentDTO, Department.class);
        return Y9Result.success(y9DepartmentService.saveOrUpdate(department));
    }

    /**
     * 删除部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> delete(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.delete(departmentId);
        return Y9Result.success();
    }

    /**
     * 禁用部门
     *
     * @param tenantId 租户id
     * @param departmentId 部门id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> disable(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9DepartmentService.changeDisable(departmentId);

        return Y9Result.success();
    }

    /**
     * 根据id获得部门对象
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是部门对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<Department> get(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentService.findById(departmentId).orElse(null));
    }

    /**
     * 模糊查询委办局列表
     *
     * @param tenantId 租户id
     * @param name 委办局名称
     * @return{@code Y9Result<List<Department>>} 通用请求返回对象 - data 是委办局集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<Department>> listBureauByNameLike(@NotBlank String tenantId, String name) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(y9DepartmentService.listBureauByNameLike(name, Boolean.FALSE));
    }

    /**
     * 根据id列表获得部门对象列表
     *
     * @param tenantId 租户id
     * @param ids 部门唯一标识结合
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listByIds(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("ids") @NotEmpty List<String> ids) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentService.list(ids));
    }

    /**
     * 根据部门名称模糊查询部门列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param name 部门名称
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listByName(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("name") @NotBlank String name) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentService.listByNameLike(name, Boolean.FALSE));
    }

    /**
     * 获取下一级部门列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 部门唯一标识
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentService.listByParentId(parentId, Boolean.FALSE));
    }

    /**
     * 根据组织节点id查找管理的部门部门属性配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织节点id
     * @param category 配置类型 {@link DepartmentPropCategoryEnum}
     * @return {@code Y9Result<List<DepartmentProp>>} 通用请求返回对象 - data 是部门属性配置集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<DepartmentProp>> listDepartmentPropByOrgUnitIdAndCategory(
        @RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("orgUnitId") @NotBlank String orgUnitId,
        @RequestParam("category") DepartmentPropCategoryEnum category) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentPropService.listByOrgBaseIdAndCategory(orgUnitId, category));
    }

    /**
     * 向上递归获取部门属性对应组织节点列表
     *
     * @param tenantId 租户id
     * @param departmentId 部门唯一标识
     * @param category 部门属性类型
     * @return {@code Y9Result<List<OrgUnit>>} 通用请求返回对象 - data 是人员或岗位对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OrgUnit>> listDepartmentPropOrgUnits(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("departmentId") @NotBlank String departmentId, @RequestParam("category") Integer category,
        @RequestParam(name = "isInherit", required = false) Boolean isInherit) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result
            .success(y9DepartmentService.listDepartmentPropOrgUnits(departmentId, category, isInherit, Boolean.FALSE));
    }

    /**
     * 递归获得所有层级子部门列表（不包含禁用）
     *
     * @param tenantId 租户id
     * @param parentId 组织节点唯一标识(可能是机构ID,也可能是部门ID)
     * @return {@code Y9Result<List<Department>>} 通用请求返回对象 - data 是部门对象集合
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> listRecursivelyByParentId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("parentId") @NotBlank String parentId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(y9DepartmentService.listRecursivelyByParentId(parentId, Boolean.FALSE));
    }

}
