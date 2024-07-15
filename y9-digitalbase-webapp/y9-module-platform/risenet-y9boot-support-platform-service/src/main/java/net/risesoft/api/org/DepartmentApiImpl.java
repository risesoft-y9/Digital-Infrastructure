package net.risesoft.api.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.dto.CreateDepartmentDTO;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9DepartmentProp;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.DepartmentProp;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9DepartmentPropService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.util.ModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

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
     * @param department 部门对象
     * @return {@code Y9Result<Department>} 通用请求返回对象 - data 是保存的部门
     * @since 9.6.0
     */
    @Override
    public Y9Result<Department> create(@RequestParam("tenantId") @NotBlank String tenantId,
        @Validated @RequestBody CreateDepartmentDTO department) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Y9Department y9Department = Y9ModelConvertUtil.convert(department, Y9Department.class);
        y9Department = y9DepartmentService.saveOrUpdate(y9Department);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Department, Department.class));
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

        Y9Department y9Department = y9DepartmentService.findById(departmentId).orElse(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Department, Department.class));
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
        List<Y9Department> y9DepartmentList = y9DepartmentService.listBureauByNameLike(name, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
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

        List<Y9Department> y9DepartmentList = y9DepartmentService.list(ids);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
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

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByNameLike(name, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
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

        List<Y9Department> y9DepartmentList = y9DepartmentService.listByParentId(parentId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
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

        List<Y9DepartmentProp> y9DepartmentPropList =
            y9DepartmentPropService.listByOrgBaseIdAndCategory(orgUnitId, category);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentPropList, DepartmentProp.class));
    }

    @Override
    public Y9Result<List<OrgUnit>> listDepartmentPropOrgUnits(String tenantId, String departmentId, Integer category) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OrgBase> y9OrgBaseList =
            y9DepartmentService.listDepartmentPropOrgUnits(departmentId, category, Boolean.FALSE);
        return Y9Result.success(ModelConvertUtil.orgBaseToOrgUnit(y9OrgBaseList));
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

        List<Y9Department> y9DepartmentList = y9DepartmentService.listRecursivelyByParentId(parentId, Boolean.FALSE);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9DepartmentList, Department.class));
    }

}
