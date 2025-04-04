package net.risesoft.controller.org;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.org.vo.OrgTreeNodeVO;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;

/**
 * 部门管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/dept", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsAnyManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class DeptController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;

    /**
     * 禁用/解除禁用部门
     *
     * @param id 部门id
     * @return {@code Y9Result<Y9Department>}
     */
    @RiseLog(operationName = "禁用/解除禁用部门", operationType = OperationTypeEnum.DELETE)
    @RequestMapping(value = "/changeDisabled")
    public Y9Result<Y9Department> changeDisabled(@RequestParam @NotBlank String id) {
        Y9Department dept = y9DepartmentService.changeDisable(id);
        return Y9Result.success(dept, "部门禁用状态修改成功！");
    }

    /**
     * 获取部门信息
     *
     * @param deptId 部门id
     * @return {@code Y9Result<Y9Department>}
     */
    @RiseLog(operationName = "根据部门id，获取部门信息")
    @RequestMapping(value = "/getDepartmentById")
    public Y9Result<Y9Department> getDepartmentById(@RequestParam @NotBlank String deptId) {
        Y9Department dept = y9DepartmentService.getById(deptId);
        return Y9Result.success(dept, "获取部门信息成功");
    }

    /**
     * 根据部门id，获取扩展属性
     *
     * @param deptId 部门id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam @NotBlank String deptId) {
        String properties = y9DepartmentService.getById(deptId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功");
    }

    /**
     * 获取部门属性对应组织节点列表
     *
     * @param deptId 部门id
     * @param category 部门属性类型
     * @return {@code Y9Result<List<Y9OrgBase>>}
     * @since 9.6.5
     */
    @RiseLog(operationName = "获取部门属性对应组织节点列表")
    @RequestMapping(value = "/listDepartmentPropOrgUnits")
    public Y9Result<List<Y9OrgBase>> listDepartmentPropOrgUnits(@RequestParam @NotBlank String deptId,
        @RequestParam Integer category) {
        return Y9Result.success(y9DepartmentService.listDepartmentPropOrgUnits(deptId, category, Boolean.FALSE, null),
            "获取部门领导列表成功");
    }

    /**
     * 获取可继承的部门属性对应组织节点列表
     *
     * @param deptId 部门id
     * @param category 部门属性类型
     * @return {@code Y9Result<List<Y9OrgBase>>}
     * @since 9.6.8
     */
    @RiseLog(operationName = "获取可继承的部门属性对应组织节点列表")
    @RequestMapping(value = "/listInheritableDepartmentPropOrgUnits")
    public Y9Result<List<Y9OrgBase>> listInheritableDepartmentPropOrgUnits(@RequestParam @NotBlank String deptId,
        @RequestParam Integer category) {
        return Y9Result.success(y9DepartmentService.listInheritableDepartmentPropOrgUnits(deptId, category, null),
            "获取部门领导列表成功");
    }

    /**
     * 获取部门排序列表
     *
     * @param parentId 父节点id
     * @return {@code Y9Result<List<Y9OrgBase>>}
     * @since 9.6.8
     */
    @RiseLog(operationName = "获取部门排序列表")
    @RequestMapping(value = "/listOrderDepts")
    public Y9Result<List<OrgTreeNodeVO>> listOrderDepts(@RequestParam @NotBlank String parentId) {
        List<Y9OrgBase> deptList = compositeOrgBaseService.getTree(parentId, OrgTreeTypeEnum.TREE_TYPE_ORG, false);

        return Y9Result.success(
            OrgTreeNodeVO.convertY9OrgBaseList(deptList, OrgTreeTypeEnum.TREE_TYPE_ORG, false, compositeOrgBaseService),
            "获取机构树成功！");
    }

    /**
     * 移动部门到新的节点
     *
     * @param deptId 部门id
     * @param parentId 新部门父节点id
     * @return {@code Y9Result<Y9Department>}
     */
    @RiseLog(operationName = "移动部门", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/move")
    public Y9Result<Y9Department> move(@RequestParam @NotBlank String deptId, @RequestParam @NotBlank String parentId) {
        Y9Department orgDept = y9DepartmentService.move(deptId, parentId);
        return Y9Result.success(orgDept, "移动部门成功");
    }

    /**
     * 根据主键id删除部门实例
     *
     * @param deptId 部门id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "删除部门", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam @NotBlank String deptId) {
        y9DepartmentService.delete(deptId);
        return Y9Result.successMsg("删除部门成功");
    }

    /**
     * 移除部门属性
     * 
     * @param deptId 部门id
     * @param category 部门属性类型
     * @param orgBaseId 人员ids
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "移除部门属性", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeDepartmentProp")
    public Y9Result<String> removeDepartmentProp(@RequestParam @NotBlank String deptId, @RequestParam Integer category,
        @RequestParam @NotBlank String orgBaseId) {
        y9DepartmentService.removeDepartmentProp(deptId, category, orgBaseId);
        return Y9Result.successMsg("移除部门领导成功");
    }

    /**
     * 保存扩展属性(直接覆盖)
     *
     * @param deptId 部门id
     * @param properties 扩展属性，json字符串
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "新增扩展属性", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam @NotBlank String deptId,
        @RequestParam String properties) {
        Y9Department orgDept = y9DepartmentService.saveProperties(deptId, properties);
        return Y9Result.success(orgDept.getProperties(), "新增扩展属性成功");
    }

    /**
     * 新建或者更新部门信息
     *
     * @param dept 部门实体
     * @return {@code Y9Result<Y9Department>}
     */
    @RiseLog(operationName = "保存部门信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Department> saveOrUpdate(@Validated Y9Department dept) {
        Y9Department returnDept = y9DepartmentService.saveOrUpdate(dept);
        return Y9Result.success(returnDept, "保存成功");
    }

    /**
     * 保存部门排序
     *
     * @param deptIds 部门下成员ids
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存部门排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam(value = "deptIds") @NotEmpty List<String> deptIds) {
        compositeOrgBaseService.sort(deptIds);
        return Y9Result.successMsg("保存部门排序成功");
    }

    /**
     * 设置部门属性的组织节点
     *
     * @param deptId 部门id
     * @param category 部门属性类型
     * @param orgBaseIds 人员ids
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "设置部门属性的组织节点", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/setDepartmentPropOrgUnits")
    public Y9Result<String> setDepartmentPropOrgUnits(@RequestParam @NotBlank String deptId, Integer category,
        @RequestParam(value = "orgBaseIds") @NotEmpty List<String> orgBaseIds) {
        y9DepartmentService.setDepartmentPropOrgUnits(deptId, category, orgBaseIds);
        return Y9Result.successMsg("设置部门领导成功");
    }

}
