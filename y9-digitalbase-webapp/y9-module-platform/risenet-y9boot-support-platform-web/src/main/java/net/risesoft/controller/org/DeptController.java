package net.risesoft.controller.org;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.OrgTreeTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
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
@IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class DeptController {

    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9DepartmentService y9DepartmentService;

    /**
     * 禁用/解除禁用部门
     *
     * @param deptId 部门id
     * @return
     */
    @RiseLog(operationName = "禁用/解除禁用部门", operationType = OperationTypeEnum.DELETE)
    @RequestMapping(value = "/changeDisabled")
    public Y9Result<Y9Department> changeDisabled(@RequestParam @NotBlank String deptId) {
        Y9Department dept = y9DepartmentService.changeDisable(deptId);
        return Y9Result.success(dept, "禁用部门成功！");
    }

    /**
     * 获取部门信息
     *
     * @param deptId 部门id
     * @return
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
     * @return
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam @NotBlank String deptId) {
        String properties = y9DepartmentService.getById(deptId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功");
    }

    /**
     * 获取部门领导列表
     *
     * @param deptId 部门id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据部门id，获取部门领导列表")
    @RequestMapping(value = "/listDeptLeaders")
    public Y9Result<List<Y9OrgBase>> listDeptLeaders(@RequestParam @NotBlank String deptId) {
        return Y9Result.success(y9DepartmentService.listLeaders(deptId), "获取部门领导列表成功");
    }

    /**
     * 根据部门id获取部门的主管领导列表
     *
     * @param deptId 部门id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据部门id获取部门的主管领导列表")
    @RequestMapping(value = "/listDeptManagers")
    public Y9Result<List<Y9OrgBase>> listDeptManagers(@RequestParam @NotBlank String deptId) {
        return Y9Result.success(y9DepartmentService.listManagers(deptId), "获取部门的主管领导列表成功");
    }

    /**
     * 获取部门排序列表
     *
     * @param parentId 父节点id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取部门排序列表")
    @RequestMapping(value = "/listOrderDepts")
    public Y9Result<List<Y9OrgBase>> listOrderDepts(@RequestParam @NotBlank String parentId) {
        List<Y9OrgBase> deptList = compositeOrgBaseService.getTree(parentId, OrgTreeTypeEnum.TREE_TYPE_ORG, false);
        return Y9Result.success(deptList, "获取数据成功");
    }

    /**
     * 移动部门到新的节点
     *
     * @param deptId 部门id
     * @param parentId 新部门父节点id
     * @return
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
     * @return
     */
    @RiseLog(operationName = "删除部门", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam @NotBlank String deptId) {
        y9DepartmentService.delete(deptId);
        return Y9Result.successMsg("删除部门成功");
    }

    /**
     * 移除部门领导
     *
     * @param deptId 部门id
     * @param orgBaseId 部门领导id
     */
    @RiseLog(operationName = "移除部门领导", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeLeader")
    public Y9Result<String> removeLeader(@RequestParam @NotBlank String deptId,
        @RequestParam @NotBlank String orgBaseId) {
        y9DepartmentService.removeLeader(deptId, orgBaseId);
        return Y9Result.successMsg("移除部门领导成功");
    }

    /**
     * 移除主管领导
     *
     * @param deptId 部门id
     * @param orgBaseId 主管领导id
     */
    @RiseLog(operationName = "移除部门主管领导", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeManager")
    public Y9Result<String> removeManager(@RequestParam @NotBlank String deptId,
        @RequestParam @NotBlank String orgBaseId) {
        y9DepartmentService.removeManager(deptId, orgBaseId);
        return Y9Result.successMsg("移除部门主管领导成功");
    }

    /**
     * 移除部门秘书
     *
     * @param deptId 部门id
     * @param personId 部门秘书id
     */
    @RiseLog(operationName = "移除部门秘书", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeSecretary")
    public Y9Result<String> removeSecretary(@RequestParam @NotBlank String deptId,
        @RequestParam @NotBlank String personId) {
        y9DepartmentService.removeSecretary(deptId, personId);
        return Y9Result.successMsg("移除部门秘书成功");
    }

    /**
     * 移除部门副领导
     *
     * @param deptId 部门id
     * @param personId 部门副领导id
     */
    @RiseLog(operationName = "移除部门副领导", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeViceLeader")
    public Y9Result<String> removeViceLeader(@RequestParam @NotBlank String deptId,
        @RequestParam @NotBlank String personId) {
        y9DepartmentService.removeViceLeader(deptId, personId);
        return Y9Result.successMsg("移除部门副领导成功");
    }

    /**
     * 保存扩展属性(直接覆盖)
     *
     * @param deptId 部门id
     * @param properties 扩展属性，json字符串
     */
    @RiseLog(operationName = "新增扩展属性", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam @NotBlank String deptId,
        @RequestParam String properties) {
        Y9Department orgDept = y9DepartmentService.saveProperties(deptId, properties);
        return Y9Result.success(orgDept.getProperties(), "新增扩展属性成功");
    }

    /**
     * 保存部门排序
     *
     * @param deptIds 部门下成员ids
     * @return
     */
    @RiseLog(operationName = "保存部门排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam(value = "deptIds") @NotEmpty List<String> deptIds) {
        compositeOrgBaseService.sort(deptIds);
        return Y9Result.successMsg("保存部门排序成功");
    }

    /**
     * 新建或者更新部门信息
     *
     * @param dept 部门实体
     * @return
     */
    @RiseLog(operationName = "保存部门信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Department> saveOrUpdate(@Validated Y9Department dept) {
        Y9Department returnDept = y9DepartmentService.saveOrUpdate(dept);
        return Y9Result.success(returnDept, "保存成功");
    }

    /**
     * 设置部门领导
     *
     * @param deptId 部门id
     * @param orgBaseIds 人员ids
     */
    @RiseLog(operationName = "设置部门领导", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/setDeptLeaders")
    public Y9Result<String> setDeptLeaders(@RequestParam @NotBlank String deptId,
        @RequestParam(value = "orgBaseIds") @NotEmpty List<String> orgBaseIds) {
        y9DepartmentService.setDeptLeaders(deptId, orgBaseIds);
        return Y9Result.successMsg("设置部门领导成功");
    }

    /**
     * 设置部门主管领导
     *
     * @param deptId 部门id
     * @param orgBaseIds 人员ids
     */
    @RiseLog(operationName = "设置部门主管领导", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/setDeptManagers")
    public Y9Result<String> setDeptManagers(@RequestParam @NotBlank String deptId,
        @RequestParam(value = "orgBaseIds") @NotEmpty List<String> orgBaseIds) {
        y9DepartmentService.setDeptManagers(deptId, orgBaseIds);
        return Y9Result.successMsg("设置部门主管领导成功");
    }

    /**
     * 设置部门秘书
     *
     * @param deptId 部门id
     * @param orgBaseIds 人员ids
     */
    @RiseLog(operationName = "设置部门秘书", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/setDeptSecretarys")
    public Y9Result<String> setDeptSecretarys(@RequestParam @NotBlank String deptId,
        @RequestParam(value = "orgBaseIds") @NotEmpty List<String> orgBaseIds) {
        y9DepartmentService.setDeptSecretarys(deptId, orgBaseIds);
        return Y9Result.successMsg("设置部门秘书成功");
    }

    /**
     * 设置部门副领导
     *
     * @param deptId 部门id
     * @param orgBaseIds 人员ids
     */
    @RiseLog(operationName = "设置部门副领导", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/setDeptViceLeaders")
    public Y9Result<String> setDeptViceLeaders(@RequestParam @NotBlank String deptId,
        @RequestParam(value = "orgBaseIds") @NotEmpty List<String> orgBaseIds) {
        y9DepartmentService.setDeptViceLeaders(deptId, orgBaseIds);
        return Y9Result.successMsg("设置部门副领导成功");
    }

}
